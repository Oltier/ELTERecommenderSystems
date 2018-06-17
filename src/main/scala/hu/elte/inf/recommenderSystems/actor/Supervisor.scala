package hu.elte.inf.recommenderSystems.actor

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props}
import hu.elte.inf.recommenderSystems.actor.RegistrationQueueListener.{CloseYourEars, Listen}
import hu.elte.inf.recommenderSystems.actor.Supervisor.{Begin, End, SendMessage}
import spray.json.JsValue

object Supervisor {
  case object Begin
  case object End
  case class SendMessage(queueName: String, message: JsValue)
  def props: Props = Props[Supervisor]
}

class Supervisor extends Actor with ActorLogging {
  val queueListener: ActorRef = context.actorOf(RegistrationQueueListener.props)

  override val supervisorStrategy: OneForOneStrategy = OneForOneStrategy(loggingEnabled = false) {
    case ex: Exception =>
      ex.printStackTrace()
      log.warning(ex.getMessage)
      Stop
  }

  override def receive: Receive = {
    case Begin => queueListener ! Listen
    case msg: SendMessage => queueListener forward msg
    case End => queueListener ! CloseYourEars
  }
}
