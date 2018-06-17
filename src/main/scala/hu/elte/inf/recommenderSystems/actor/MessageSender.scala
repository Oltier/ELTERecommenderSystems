package hu.elte.inf.recommenderSystems.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.spingo.op_rabbit._
import hu.elte.inf.recommenderSystems.actor.MessageSender.SendMessage
import org.json4s.DefaultJsonFormats
import spray.json.{DefaultJsonProtocol, JsValue}

import scala.concurrent.ExecutionContextExecutor

object MessageSender {

  case class SendMessage(queueName: String, message: JsValue)

  def props(rabbitControl: ActorRef): Props = Props(new MessageSender(rabbitControl))

  val name: String = "MessageSender"
}

class MessageSender(rabbitControl: ActorRef)
    extends Actor
    with ActorLogging
    with DefaultJsonProtocol
    with DefaultJsonFormats
    with LimitedDeliveryStrategy {

  import com.spingo.op_rabbit.SprayJsonSupport._

  implicit val ec: ExecutionContextExecutor = context.system.dispatcher

  var myQueueSubscription: Option[SubscriptionRef] = None

  override def receive: Receive = {
    case msg: SendMessage =>
      rabbitControl ! Message.topic(msg.message, msg.queueName)
  }
}
