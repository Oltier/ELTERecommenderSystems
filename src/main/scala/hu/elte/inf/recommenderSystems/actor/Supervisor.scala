package hu.elte.inf.recommenderSystems.actor

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props}
import com.spingo.op_rabbit.{Binding, RabbitControl, RecoveryStrategy}
import hu.elte.inf.recommenderSystems.actor.MessageSender.SendMessage
import hu.elte.inf.recommenderSystems.actor.Supervisor.{Begin, End}

object Supervisor {
  case object Begin
  case object End

  def props: Props = Props[Supervisor]
}

class Supervisor extends Actor with ActorLogging {
  val rabbitControl: ActorRef = context.actorOf(Props[RabbitControl])
  val registrationQueueListener: ActorRef = context.actorOf(RegistrationQueueListener.props(rabbitControl), RegistrationQueueListener.name)
  val nodeAndRelationQueueListener: ActorRef = context.actorOf(NodeAndRelationQueueListener.props(rabbitControl), NodeAndRelationQueueListener.name)
  val messageSender: ActorRef = context.actorOf(MessageSender.props(rabbitControl), MessageSender.name)

  override val supervisorStrategy: OneForOneStrategy = OneForOneStrategy(loggingEnabled = false) {
    case ex: Exception =>
      ex.printStackTrace()
      log.warning(ex.getMessage)
      Stop
  }

  override def receive: Receive = {
    case Begin =>
      registrationQueueListener ! Listen
      nodeAndRelationQueueListener ! Listen

    case msg: SendMessage => messageSender forward msg

    case End =>
      registrationQueueListener ! CloseYourEars
      nodeAndRelationQueueListener ! CloseYourEars
  }
}
