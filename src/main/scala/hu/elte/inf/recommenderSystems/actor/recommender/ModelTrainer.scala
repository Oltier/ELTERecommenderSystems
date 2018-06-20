package hu.elte.inf.recommenderSystems.actor.recommender

import akka.actor.{Actor, ActorLogging, Props}
import hu.elte.inf.recommenderSystems.actor.recommender.RecommenderSystem.UpdateModel
import hu.elte.inf.recommenderSystems.model.MovieLensData
import org.apache.spark.SparkContext
import org.apache.spark.mllib.recommendation.{ALS, MatrixFactorizationModel}

object ModelTrainer {
  case class RunTraining(movieLensData: MovieLensData)

  def props(sc: SparkContext, rank: Int, iterations: Int, lambda: Double): Props = Props(new ModelTrainer(sc, rank, iterations, lambda))
}

class ModelTrainer(sc: SparkContext, rank: Int, iterations: Int, lambda: Double) extends Actor with ActorLogging {
  import ModelTrainer._

  override def receive: Receive = {
    case RunTraining(movieLensData) =>
      trainModel(movieLensData)
  }

  private def trainModel(data: MovieLensData): Unit = {
    val model: MatrixFactorizationModel = ALS.train(data.loadRatings().values, rank, iterations, lambda)
    sender ! UpdateModel(model)
    context.stop(self)
  }
}
