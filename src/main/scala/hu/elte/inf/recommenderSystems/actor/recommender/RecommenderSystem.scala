package hu.elte.inf.recommenderSystems.actor.recommender

import akka.actor.{Actor, ActorLogging, Props}
import hu.elte.inf.recommenderSystems.actor.recommender.ModelTrainer.RunTraining
import hu.elte.inf.recommenderSystems.model.MovieLensData
import org.apache.spark.SparkContext
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel

object RecommenderSystem {
  case class Train(movieLensData: MovieLensData)
  case class GenerateRecommendations(userId: Int, count: Int)
  case class UpdateModel(model: MatrixFactorizationModel)

  def props(sc: SparkContext) = Props(new RecommenderSystem(sc))
  val name: String = "ALS_RecommenderSystem"
}

class RecommenderSystem(sc: SparkContext) extends Actor with ActorLogging {

  import RecommenderSystem._

  var model: Option[MatrixFactorizationModel] = None

  override def receive: Receive = {
    case Train(movieLensData) =>
      trainModel(movieLensData)

    case GenerateRecommendations(userId, count) =>
      generateRecommendations(userId, count)

    case UpdateModel(model: MatrixFactorizationModel) =>
      this.model = Some(model)
  }



  private def trainModel(movieLensData: MovieLensData): Unit = {
    val modelTrainer = context.actorOf(ModelTrainer.props(sc, 10, 10, 0.01))
    modelTrainer ! RunTraining(movieLensData)
  }

  private def generateRecommendations(userId: Int, count: Int): Unit = {
    log.info(s"Generating $count recommendations for userId: $userId")

    val recommendations = model.fold[List[Recommendation]](Nil)(model => model
      .recommendProducts(userId, count)
      .map(rating => Recommendation(rating.product, rating.rating))
      .toList
    )

    log.info("Recommendation generation finished")

    sender ! Recommendations(recommendations)
  }

}
