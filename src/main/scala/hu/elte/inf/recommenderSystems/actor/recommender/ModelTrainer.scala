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

  case class ComputeRmse(model: MatrixFactorizationModel, data: RDD[Rating], n: Long)

  def props(sc: SparkContext, rank: Int, iterations: Int, lambda: Double): Props = Props(new ModelTrainer(sc, rank, iterations, lambda))
}

class ModelTrainer(sc: SparkContext, rank: Int, iterations: Int, lambda: Double) extends Actor with ActorLogging {

  import ModelTrainer._

  override def receive: Receive = {
    case RunTraining(movieLensData) =>
      trainModel(movieLensData)

    case cr: ComputeRmse =>
      computeRmse(cr)
  }

  private def trainModel(data: MovieLensData): Unit = {
    val model: MatrixFactorizationModel = ALS.train(data.loadRatings().values, rank, iterations, lambda)
    sender ! UpdateModel(model, data)
    context.stop(self)
  }

  private def computeRmse(cr: ComputeRmse) = {
    val predictions: RDD[Rating] = cr.model.predict(cr.data.map(rating => (rating.user, rating.product)))
    val predictionsAndRatings = predictions.map(rating => ((rating.user, rating.product), rating.rating))
      .join(cr.data.map(rating => ((rating.user, rating.product), rating.rating)))
      .values
    math.sqrt(predictionsAndRatings.map(x => (x._1 - x._2) * (x._1 - x._2)).reduce(_ + _) / cr.n)
  }

  private def evalALS(cr: ComputeRmse, nFolds: Int = 10, seed: Int = 77,
                      rank: Int = 10, iter: Int = 20, alpha: Double = 0.01): (Double, Array[Double], Array[MatrixFactorizationModel]) = {
    val data = cr.data
    val folds: Array[(RDD[Rating], RDD[Rating])] = kFold(data, nFolds, seed)
    val models: Array[(MatrixFactorizationModel, RDD[Rating])] = folds.map {
      case (train, test) => (ALS.train(train, rank, iter, alpha), test)
    }
    // evaluate model predictions on test data: RDD[((user, product), rating)]
    val pred = models.map {
      case (model, test) =>
        model.predict(test.map {
          case Rating(usr, prod, _) =>
            (usr, prod)
        }).map {
          case Rating(usr, prod, rate) => ((usr, prod), rate)
        }
    }
    // reformat truth: RDD[((user, product), rating)]
    val truth = models.map { case (_, test) => test.map { case Rating(usr, prod, rate) => ((usr, prod), rate) } }
    // an array of RMSE for each test fold:
    val rmse = truth.zip(pred).map { case (tr, pd) => tr.join(pd).map { case ((_, _), (t, p)) => math.pow(p - t, 2) }.mean() }.map(math.sqrt)
    // returns tuple: (total RMSE, RMSE per fold, model per fold)
    (rmse.fold(0.0)(_ + _) / rmse.length, rmse, models.map(_._1))
  }
}
