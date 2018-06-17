package hu.elte.inf.recommenderSystems.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.spingo.op_rabbit.Directives._
import com.spingo.op_rabbit._
import com.spingo.op_rabbit.SprayJsonSupport._
import hu.elte.inf.recommenderSystems.actor.MessageSender.SendMessage
import hu.elte.inf.recommenderSystems.actor.RegistrationQueueListener.{CloseYourEars, Listen}
import hu.elte.inf.recommenderSystems.model.registration.{RegistrationJsonSupport, RegistrationMessage}

import scala.concurrent.ExecutionContextExecutor

object RegistrationQueueListener {

  case object Listen

  case object CloseYourEars

  def props: Props = Props(new RegistrationQueueListener)

  val name: String = "RegistrationQueueListener"
}

class RegistrationQueueListener extends Actor with ActorLogging with RegistrationJsonSupport {
  val QUEUE: String = "ReCoEngineRegistry"

  implicit val recoveryStrategy: AnyRef with RecoveryStrategy {
    def genRetryBinding(queueName: String): Binding
  } = RecoveryStrategy.limitedRedeliver()

  implicit val ec: ExecutionContextExecutor = context.system.dispatcher

  val RABBIT_CONTROL: ActorRef = context.actorOf(Props[RabbitControl])

  var myQueueSubscription: Option[SubscriptionRef] = None

  override def receive: Receive = {
    case Listen =>
      myQueueSubscription = Some(
        Subscription.run(RABBIT_CONTROL) {
          channel(qos = 3) {
            consume(topic(queue(QUEUE), List(s"$QUEUE.#"))) {
              (body(as[RegistrationMessage]) & routingKey) {
                (obj, key) =>
                  log.debug(s"received my object $obj with key $key")
                  ack
              }
            }
          }
        }
      )

    case CloseYourEars =>
      myQueueSubscription.foreach(_.close())
  }
}
