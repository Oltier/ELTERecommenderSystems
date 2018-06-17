package hu.elte.inf.recommenderSystems.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.spingo.op_rabbit._
import hu.elte.inf.recommenderSystems.actor.MessageSender.SendMessage
import org.json4s.DefaultJsonFormats
import spray.json.{DefaultJsonProtocol, JsValue}

import scala.concurrent.ExecutionContextExecutor

object MessageSender {

  case class SendMessage(queueName: String, message: JsValue)

  def props: Props = Props(new MessageSender)

  val name: String = "MessageSender"
}

class MessageSender
    extends Actor
    with ActorLogging
    with DefaultJsonProtocol
    with DefaultJsonFormats {

  import com.spingo.op_rabbit.SprayJsonSupport._

  implicit val recoveryStrategy: AnyRef with RecoveryStrategy {
    def genRetryBinding(queueName: String): Binding
  } = RecoveryStrategy.limitedRedeliver()

  implicit val ec: ExecutionContextExecutor = context.system.dispatcher

  val RABBIT_CONTROL: ActorRef = context.actorOf(Props[RabbitControl])

  var myQueueSubscription: Option[SubscriptionRef] = None

  override def receive: Receive = {
    case msg: SendMessage =>
      RABBIT_CONTROL ! Message.topic(msg.message, msg.queueName)
  }
}
