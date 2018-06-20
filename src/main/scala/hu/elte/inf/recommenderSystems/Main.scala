package hu.elte.inf.recommenderSystems

import java.util.UUID

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import hu.elte.inf.recommenderSystems.actor.MessageSender.{SendJsonMessage, SendMessageWithCorrelationIdAndReplyTo, SendMessageWithCorrelationIdAndReplyToHelloWorld}
import hu.elte.inf.recommenderSystems.actor.Supervisor.{Begin, End}
import hu.elte.inf.recommenderSystems.actor.recommender.ModelTrainer.TuneParameters
import hu.elte.inf.recommenderSystems.actor.recommender.RecommenderSystem
import hu.elte.inf.recommenderSystems.actor.recommender.RecommenderSystem.{GenerateRecommendations, Train}
import hu.elte.inf.recommenderSystems.actor.{HelloWorldListener, NodeAndRelationQueueListener, RegistrationQueueListener, Supervisor}
import hu.elte.inf.recommenderSystems.config.{Config, SparkConfig}
import hu.elte.inf.recommenderSystems.model.MovieLensData
import hu.elte.inf.recommenderSystems.model.entityrelation.{AddNodeType, AddNodeTypeMethod, EntityRelationJsonSupport}
import hu.elte.inf.recommenderSystems.model.enum.Method
import hu.elte.inf.recommenderSystems.model.knowledgebase._
import hu.elte.inf.recommenderSystems.model.mychannel.{GetRecommendations, GetRecommendationsMethod, MyChannelJsonSupport}
import hu.elte.inf.recommenderSystems.model.registration.{RegistrationJsonSupport, RegistrationMessage, RegistrationTask}
import hu.elte.inf.recommenderSystems.utils.DirectoryPathUtil
import spray.json._

import scala.concurrent.Await
import scala.concurrent.duration._

object Main
    extends App
    with RegistrationJsonSupport
    with EntityRelationJsonSupport
    with KnowledgeBaseJsonSupport
    with MyChannelJsonSupport {

  if (Config.SETUP.rabbitMqMode) {
    val system: ActorSystem = ActorSystem("rabbitMq")
    val supervisor = system.actorOf(Supervisor.props)
    supervisor ! Begin

    val registrationMessage = RegistrationMessage(
      Method.REGISTER,
      RegistrationTask("tudlik_zoltan_ce0ta3", Config.DEVELOPER))
    val addNodeTypeMessage =
      AddNodeTypeMethod(Method.ADD_NODE_TYPE, AddNodeType("Person"))
    val helloWorldMsg = SendMessageWithCorrelationIdAndReplyToHelloWorld(
      HelloWorldListener.QUEUE,
      "Zoli",
      UUID.randomUUID().toString,
      HelloWorldListener.QUEUE)
    val knowledgeBaseEmitMethod = KnowledgeBaseEmitMethod(
      Method.EMIT,
      KnowledgeBaseEmit(Config.QUEUE.myChannel))
    val trueKnowledgeBaseEmitMethod = TrueKnowledgeBaseEmitMethod(
      Method.EMIT,
      TrueKnowledgeBaseEmit(channel = Config.QUEUE.myChannel, itemCount = 1000))
    val myChannelMessage = GetRecommendationsMethod(
      Method.GET_RECOMMENDATIONS,
      GetRecommendations(externalId = 123, maxResults = 10))

    var cmd: String = null

    do {
      cmd = scala.io.StdIn.readLine(
        "Available commands: register, addNode, helloWorld, knowledgeBaseEmit, trueKnowledgeBaseEmit, myChannelMessage, exit\n")

      cmd match {
        case "register" =>
          supervisor ! SendJsonMessage(RegistrationQueueListener.QUEUE,
                                       registrationMessage.toJson)

        case "addNode" =>
          supervisor ! SendJsonMessage(NodeAndRelationQueueListener.QUEUE,
                                       addNodeTypeMessage.toJson)

        case "helloWorld" =>
          supervisor ! helloWorldMsg

        case "knowledgeBaseEmit" =>
          supervisor ! SendJsonMessage(Config.QUEUE.knowledgeBaseQueueName,
                                       knowledgeBaseEmitMethod.toJson)

        case "trueKnowledgeBaseEmit" =>
          supervisor ! SendJsonMessage(Config.QUEUE.trueRatingQueueName,
                                       trueKnowledgeBaseEmitMethod.toJson)

        case "myChannelMessage" =>
          supervisor ! SendMessageWithCorrelationIdAndReplyTo(
            Config.QUEUE.myChannel,
            myChannelMessage.toJson,
            UUID.randomUUID().toString,
            HelloWorldListener.QUEUE)

        case other => println(s"Unknown commend: $other. Type 'exit' to quit.")
      }
    } while (cmd != "exit")

    supervisor ! End

    system.terminate()

  } else {
    val system: ActorSystem = ActorSystem("FileRecommenderSystem")

    val recommenderSystem =
      system.actorOf(RecommenderSystem.props(SparkConfig.sc))
    val movieLensData = new MovieLensData(
      DirectoryPathUtil.getResourceDirectoryPath(Config.SPARK.sourceFolder))
    implicit val timeout: Timeout = 30.seconds
    implicit val awaitDuration: FiniteDuration = 3.seconds

    var cmd: String = null

    do {
      cmd = scala.io.StdIn.readLine(
        "Available commands: train, tuneParameters, getRecommendation [uid: Int] [count: Int]\n")

      cmd match {
        case "train" =>
          recommenderSystem ! Train(movieLensData)

        case "tuneParameters" =>
          recommenderSystem ! TuneParameters(movieLensData)

        case other =>
          if (other.startsWith("getRecommendation")) {
            val params = other.split(" ")
            if(params.size == 3) {
              val uid = params(1).toInt
              val count = params(2).toInt
              val recommendations = Await.result(recommenderSystem ? GenerateRecommendations(uid, count), awaitDuration)
              println(recommendations)
            } else {
              println("format: getRecommendation [uid: Int] [count: Int]")
            }

          } else {
            println(s"Unknown command: $other. Type 'exit' to quit.")
          }
      }
    } while (cmd != "exit")

    system.terminate()
  }



}
