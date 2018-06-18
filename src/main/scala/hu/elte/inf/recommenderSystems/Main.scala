package hu.elte.inf.recommenderSystems

import java.util.UUID

import akka.actor.ActorSystem
import hu.elte.inf.recommenderSystems.actor.MessageSender.{SendJsonMessage, SendMessageWithCorrelationIdAndReplyToHelloWorld}
import spray.json._
import hu.elte.inf.recommenderSystems.actor.{HelloWorldListener, NodeAndRelationQueueListener, RegistrationQueueListener, Supervisor}
import hu.elte.inf.recommenderSystems.actor.Supervisor.{Begin, End}
import hu.elte.inf.recommenderSystems.config.Config
import hu.elte.inf.recommenderSystems.model.entityrelation.{AddNodeType, AddNodeTypeMethod, EntityRelationJsonSupport}
import hu.elte.inf.recommenderSystems.model.enum.Method
import hu.elte.inf.recommenderSystems.model.knowledgebase.{KnowledgeBaseEmit, KnowledgeBaseEmitMethod, KnowledgeBaseJsonSupport}
import hu.elte.inf.recommenderSystems.model.registration.{RegistrationJsonSupport, RegistrationMessage, RegistrationTask}

object Main extends App with RegistrationJsonSupport with EntityRelationJsonSupport with KnowledgeBaseJsonSupport {

  val system: ActorSystem = ActorSystem("rabbitMq")

  val supervisor = system.actorOf(Supervisor.props)
  supervisor ! Begin

  if (Config.SETUP.enableSendRegisterMessage) {

    val registrationMessage = RegistrationMessage(Method.REGISTER, RegistrationTask("tudlik_zoltan_ce0ta3", Config.DEVELOPER))
    val addNodeTypeMessage = AddNodeTypeMethod(Method.ADD_NODE_TYPE, AddNodeType("Person"))
    val helloWorldMsg = SendMessageWithCorrelationIdAndReplyToHelloWorld(HelloWorldListener.QUEUE, "Zoli", UUID.randomUUID().toString, HelloWorldListener.QUEUE)
    val knowledgeBaseEmitMethod = KnowledgeBaseEmitMethod(Method.EMIT, KnowledgeBaseEmit("UnicornsAreAwesome"))

    supervisor ! SendJsonMessage("KnowledgeBaseService#ml_1m", knowledgeBaseEmitMethod.toJson)

//    supervisor ! SendJsonMessage(RegistrationQueueListener.QUEUE, registrationMessage.toJson)
//    supervisor ! SendJsonMessage(NodeAndRelationQueueListener.QUEUE, addNodeTypeMessage.toJson)
//    supervisor ! helloWorldMsg

  }

  scala.io.StdIn.readLine("Press any key to stop the system\n")

  supervisor ! End

  system.terminate()

}
