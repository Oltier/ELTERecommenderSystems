package hu.elte.inf.recommenderSystems

import akka.actor.ActorSystem
import hu.elte.inf.recommenderSystems.actor.MessageSender.SendMessage
import spray.json._
import hu.elte.inf.recommenderSystems.actor.{NodeAndRelationQueueListener, RegistrationQueueListener, Supervisor}
import hu.elte.inf.recommenderSystems.actor.Supervisor.{Begin, End}
import hu.elte.inf.recommenderSystems.config.Config
import hu.elte.inf.recommenderSystems.model.entityrelation.{AddNodeType, AddNodeTypeMethod, EntityRelationJsonSupport}
import hu.elte.inf.recommenderSystems.model.enum.Method
import hu.elte.inf.recommenderSystems.model.registration.{RegistrationJsonSupport, RegistrationMessage, RegistrationTask}

object Main extends App with RegistrationJsonSupport with EntityRelationJsonSupport {

  val system: ActorSystem = ActorSystem("rabbitMq")

  val supervisor = system.actorOf(Supervisor.props)
  supervisor ! Begin

  if (Config.SETUP.enableSendRegisterMessage) {

    val registrationMessage = RegistrationMessage(Method.REGISTER, RegistrationTask("tudlik_zoltan_ce0ta3", Config.DEVELOPER))
    val addNodeTypeMessage = AddNodeTypeMethod(Method.ADD_NODE_TYPE, AddNodeType("Person"))

    supervisor ! SendMessage(RegistrationQueueListener.QUEUE, registrationMessage.toJson)
    supervisor ! SendMessage(NodeAndRelationQueueListener.QUEUE, addNodeTypeMessage.toJson)

  }

  scala.io.StdIn.readLine("Press any key to stop the system\n")

  supervisor ! End

  system.terminate()

}
