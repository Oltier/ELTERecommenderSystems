package hu.elte.inf.recommenderSystems.actor.recommender

import akka.actor.{Actor, ActorLogging, Props}
import hu.elte.inf.recommenderSystems.actor.recommender.RecommenderSystem.UpdateModel
import hu.elte.inf.recommenderSystems.model.MovieLensData
import org.apache.spark.SparkContext
import org.apache.spark.mllib.recommendation.{ALS, MatrixFactorizationModel, Rating}
import org.apache.spark.mllib.util.MLUtils.kFold
import org.apache.spark.rdd.RDD

object ModelTrainer {

  case class RunTraining(movieLensData: MovieLensData)

  case class TuneParameters(movieLensData: MovieLensData)

  def props(sc: SparkContext): Props = Props(new ModelTrainer(sc, rank, iterations, lambda))
}

class ModelTrainer(sc: SparkContext) extends Actor with ActorLogging {

  import ModelTrainer._

  val ranks: List[Int] = (6 to 10).toList //Hidden parameters, such as sex, age, income.
  val lambdas: List[Double] = (0.1 to 0.5 by 0.1).toList
  val numIters: List[Int] = (5 to 25 by 5).toList
  var bestModel: Option[MatrixFactorizationModel] = None
  var bestValidationRmse: Double = Double.MaxValue
  var bestRank: Int = 10
  var bestLambda: Double = 0.2
  var bestNumIter: Int = 20
  var data: Option[RDD[Rating]] = None


  override def receive: Receive = {
    case RunTraining(movieLensData) =>
      this.data = Some(movieLensData.loadRatings().values)
      trainModel(movieLensData)

    case TuneParameters(movieLensData) =>
      this.data = Some(movieLensData.loadRatings().values)
      findBestConfig()

  }

  private def trainModel(movieLensData: MovieLensData): Unit = {
    bestModel = Some(ALS.train(data.get, bestRank, bestNumIter, bestLambda))
    sender ! UpdateModel(bestModel.get, movieLensData)
    log.info(s"Model trained with rank = $bestRank, lambda = $bestLambda and iter = $bestNumIter.")
  }

  private def findBestConfig(): Unit = {
    for {
      rank <- ranks
      lambda <- lambdas
      numIter <- numIters
    } {
      val validationRmse = computeRmse(data = data.get, rank = rank, iter = numIter, lambda = lambda)

      log.info(s"RMSE = $validationRmse for the model trained with rank = $rank, lambda = $lambda and iter = $numIter.")

      if(validationRmse < bestValidationRmse) {
        bestValidationRmse = validationRmse
        bestRank = rank
        bestLambda = lambda
        bestNumIter = numIter
      }
    }

    log.info(s"Best config: RMSE = $bestValidationRmse for the model trained with rank = $bestRank, lambda = $bestLambda and iter = $bestNumIter.")
  }

  private def computeRmse(data: RDD[Rating], nFolds: Int = 10, seed: Int = 77,
                          rank: Int = 10, iter: Int = 20, lambda: Double = 0.01): Double = {

    val folds: Array[(RDD[Rating], RDD[Rating])] = kFold(data, nFolds, seed)
    val models: Array[(MatrixFactorizationModel, RDD[Rating])] = folds.map(trainAndTest => (ALS.train(trainAndTest._1, rank, iter, lambda), trainAndTest._2))
    val predictions: Array[RDD[((Int, Int), Double)]] = models
      .map(modelAndTest =>
        modelAndTest._1
          .predict(modelAndTest._2.map(rating => (rating.user, rating.product)))
          .map(rating => ((rating.user, rating.product), rating.rating))
      )

    val trueRating: Array[RDD[((Int, Int), Double)]] = models
      .map(matrixFactModelAndRDD =>
        matrixFactModelAndRDD._2
          .map(rating => ((rating.user, rating.product), rating.rating))
      )

    val rmse = trueRating
      .zip(predictions)
      .map(trueRatingAndPred => {
        val trueRating = trueRatingAndPred._1
        val pred = trueRatingAndPred._2
        trueRating
          .join(pred)
          .map {
            case ((_, _), (t, p)) =>
              math.pow(p - t, 2)
          }.mean()
      }).map(math.sqrt)

    rmse.fold(0.0)(_ + _) / rmse.length
  }
}
