package hu.elte.inf.recommenderSystems.actor

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props}
import com.spingo.op_rabbit.{Binding, RabbitControl, RecoveryStrategy}
import hu.elte.inf.recommenderSystems.actor.MessageSender.{SendJsonMessage, SendMessage}
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
  val helloWorldListener: ActorRef = context.actorOf(HelloWorldListener.props(rabbitControl, messageSender), HelloWorldListener.name)
  val myChannelListener: ActorRef = context.actorOf(MyChannelListener.props(rabbitControl))

  override val supervisorStrategy: OneForOneStrategy = OneForOneStrategy(loggingEnabled = true) {
    case ex: Exception =>
      ex.printStackTrace()
      log.warning(ex.getMessage)
      Stop
  }

  override def receive: Receive = {
    case Begin =>
      registrationQueueListener ! Listen
      nodeAndRelationQueueListener ! Listen
      helloWorldListener ! Listen
      myChannelListener ! Listen

    case msg: SendMessage => messageSender forward msg

    case End =>
      registrationQueueListener ! CloseYourEars
      nodeAndRelationQueueListener ! CloseYourEars
      helloWorldListener ! CloseYourEars
      myChannelListener ! CloseYourEars
  }
}
