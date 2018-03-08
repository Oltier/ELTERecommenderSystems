package hu.elte.inf.recommenderSystems.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.spingo.op_rabbit.Directives._
import com.spingo.op_rabbit.PlayJsonSupport._
import com.spingo.op_rabbit._
import hu.elte.inf.recommenderSystems.actor.QueueListener.{CloseYourEars, Listen}
import hu.elte.inf.recommenderSystems.actor.Supervisor.SendMessage
import hu.elte.inf.recommenderSystems.config.QueueConfig
import hu.elte.inf.recommenderSystems.model.MyObject

import scala.concurrent.ExecutionContextExecutor

object QueueListener {

  case object Listen

  case object CloseYourEars

  def props: Props = Props(new QueueListener)
}

class QueueListener extends Actor with ActorLogging {
  val QUEUE: String = QueueConfig.QUEUE.name
  implicit val recoveryStrategy = RecoveryStrategy.limitedRedeliver()
  implicit val ec: ExecutionContextExecutor = context.system.dispatcher

  val RABBIT_CONTROL: ActorRef = context.actorOf(Props[RabbitControl])

  var myQueueSubscription: Option[SubscriptionRef] = None

  override def receive: Receive = {
    case Listen =>
      myQueueSubscription = Some(
        Subscription.run(RABBIT_CONTROL) {
          channel(qos = 3) {
            consume(topic(queue(QUEUE), List("some-topic.#"))) {
              (body(as[MyObject]) & routingKey) {
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
        RABBIT_CONTROL ! Message.topic(msg.myObject, "some-topic.cool")
      }

    case CloseYourEars =>
      myQueueSubscription.foreach(_.close())
  }
}
