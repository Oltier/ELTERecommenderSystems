package hu.elte.inf.recommenderSystems.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.spingo.op_rabbit._
import com.spingo.op_rabbit.properties.{CorrelationId, ReplyTo}
import hu.elte.inf.recommenderSystems.actor.MessageSender.{SendJsonMessage, SendMessageWithCorrelationId, SendMessageWithCorrelationIdAndReplyToHelloWorld, SendMessageWithCorrelationIdHelloWord}
import org.json4s.DefaultJsonFormats
import spray.json.{DefaultJsonProtocol, JsValue}

import scala.concurrent.ExecutionContextExecutor

object MessageSender {

  sealed trait SendMessage

  case class SendJsonMessage(queueName: String, message: JsValue) extends SendMessage

  case class SendMessageWithCorrelationId(queueName: String, message: JsValue, correlationId: String) extends SendMessage

  case class SendMessageWithCorrelationIdHelloWord(queueName: String, message: String, correlationId: String) extends SendMessage

  case class SendMessageWithCorrelationIdAndReplyToHelloWorld(queueName: String, message: String, correlationId: String, replyTo: String) extends SendMessage

  def props(rabbitControl: ActorRef): Props = Props(new MessageSender(rabbitControl))

  val name: String = "MessageSender"
}

class MessageSender(rabbitControl: ActorRef)
    extends Actor
    with ActorLogging
    with DefaultJsonProtocol
    with DefaultJsonFormats
    with LimitedRedeliveryStrategy {

  import com.spingo.op_rabbit.SprayJsonSupport._

  implicit val ec: ExecutionContextExecutor = context.system.dispatcher

  var myQueueSubscription: Option[SubscriptionRef] = None

  override def receive: Receive = {
    case msg: SendJsonMessage =>
      rabbitControl ! Message.topic(msg.message, msg.queueName)

    case msg: SendMessageWithCorrelationId =>
      rabbitControl ! Message.topic(msg.message, msg.queueName, properties = Seq(CorrelationId(msg.correlationId)))

    case msg: SendMessageWithCorrelationIdHelloWord =>
      rabbitControl ! Message.topic(msg.message, msg.queueName, properties = Seq(CorrelationId(msg.correlationId)))

    case msg: SendMessageWithCorrelationIdAndReplyToHelloWorld =>
      rabbitControl ! Message.topic(msg.message, msg.queueName, properties = Seq(CorrelationId(msg.correlationId), ReplyTo(msg.replyTo)))
  }
}
