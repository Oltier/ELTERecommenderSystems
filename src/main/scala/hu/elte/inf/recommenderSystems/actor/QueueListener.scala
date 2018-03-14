package hu.elte.inf.recommenderSystems.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.spingo.op_rabbit.Directives._
import com.spingo.op_rabbit.PlayJsonSupport._
import com.spingo.op_rabbit._
import hu.elte.inf.recommenderSystems.actor.QueueListener.{CloseYourEars, Listen}
import hu.elte.inf.recommenderSystems.actor.Supervisor.SendMessage
import hu.elte.inf.recommenderSystems.config.Config
import hu.elte.inf.recommenderSystems.model.RegistrationMessage

import scala.concurrent.ExecutionContextExecutor
import play.api.libs.json._

object QueueListener {

  case object Listen

  case object CloseYourEars

  def props: Props = Props(new QueueListener)
}

class QueueListener extends Actor with ActorLogging {
  val QUEUE: String = Config.QUEUE.name
  implicit val recoveryStrategy = RecoveryStrategy.limitedRedeliver()
  implicit val ec: ExecutionContextExecutor = context.system.dispatcher

  val RABBIT_CONTROL: ActorRef = context.actorOf(Props[RabbitControl])

  var myQueueSubscription: Option[SubscriptionRef] = None

  override def receive: Receive = {
    case Listen =>
      myQueueSubscription = Some(
        Subscription.run(RABBIT_CONTROL) {
          channel(qos = 3) {
            consume(topic(queue(QUEUE), List(s"${Config.QUEUE.name}.#"))) {
              (body(as[JsValue]) & routingKey) {
                (obj, key) =>
                  log.debug(s"received my object $obj with key $key")
                  ack
              }
            }
          }
        }
      )

    case msg: SendMessage =>
      myQueueSubscription.get.initialized.foreach{ _ =>
        RABBIT_CONTROL ! Message.topic(msg.message, msg.queueName)
      }

    case CloseYourEars =>
      myQueueSubscription.foreach(_.close())
  }
}
