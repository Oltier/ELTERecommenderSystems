package hu.elte.inf.recommenderSystems.actor

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props}
import spray.json.JsValue
import hu.elte.inf.recommenderSystems.actor.MessageSender.SendMessage
import hu.elte.inf.recommenderSystems.actor.RegistrationQueueListener.{CloseYourEars, Listen}
import hu.elte.inf.recommenderSystems.actor.Supervisor.{Begin, End}

object Supervisor {
  case object Begin
  case object End

  def props: Props = Props[Supervisor]
}

class Supervisor extends Actor with ActorLogging {
  val registrationQueueListener: ActorRef = context.actorOf(RegistrationQueueListener.props, RegistrationQueueListener.name)
  val messageSender: ActorRef = context.actorOf(MessageSender.props, MessageSender.name)

  override val supervisorStrategy: OneForOneStrategy = OneForOneStrategy(loggingEnabled = false) {
    case ex: Exception =>
      ex.printStackTrace()
      log.warning(ex.getMessage)
      Stop
  }

  override def receive: Receive = {
    case Begin => registrationQueueListener ! Listen

    case msg: SendMessage => messageSender forward msg

    case End => registrationQueueListener ! CloseYourEars
  }
}
