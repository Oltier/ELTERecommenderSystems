package hu.elte.inf.recommenderSystems.actor.recommender

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import hu.elte.inf.recommenderSystems.actor.recommender.ModelTrainer.RunTraining
import hu.elte.inf.recommenderSystems.model.MovieLensData
import org.apache.spark.SparkContext
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel

object RecommenderSystem {

  case class Train(movieLensData: MovieLensData)

  case class GenerateRecommendations(userId: Int, count: Int)

  case class UpdateModel(model: MatrixFactorizationModel, movieLensData: MovieLensData)

  def props(sc: SparkContext) = Props(new RecommenderSystem(sc))

  val name: String = "ALS_RecommenderSystem"
}

class RecommenderSystem(sc: SparkContext) extends Actor with ActorLogging {

  import RecommenderSystem._

  val modelTrainer: ActorRef = context.actorOf(ModelTrainer.props(sc, 10, 10, 0.01))
  var model: Option[MatrixFactorizationModel] = None
  var trainingData: Option[Map[Int, String]] = None

  override def receive: Receive = {
    case Train(movieLensData) =>
      modelTrainer ! RunTraining(movieLensData)

    case GenerateRecommendations(userId, count) =>
      generateRecommendations(userId, count)

    case UpdateModel(model: MatrixFactorizationModel, movieLensData: MovieLensData) =>
      this.model = Some(model)
      this.trainingData = Some(movieLensData.loadMovies())
  }

  private def generateRecommendations(userId: Int, count: Int): Unit = {
    log.info(s"Generating $count recommendations for userId: $userId")

    val recommendations = model.fold[List[Recommendation]](Nil)(model => model
      .recommendProducts(userId, count)
      .zipWithIndex
      .map(ratingAndId => Recommendation(ratingAndId._2 + 1, ratingAndId._1.product, trainingData.get(ratingAndId._1.product), ratingAndId._1.rating))
      .toList
    )

    log.info("Recommendation generation finished")

    sender ! Recommendations(recommendations)
  }

}
