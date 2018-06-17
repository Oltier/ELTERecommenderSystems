package hu.elte.inf.recommenderSystems.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.spingo.op_rabbit.Directives._
import com.spingo.op_rabbit._
import com.spingo.op_rabbit.SprayJsonSupport._
import hu.elte.inf.recommenderSystems.model.registration.{RegistrationJsonSupport, RegistrationMessage}

import scala.concurrent.ExecutionContextExecutor

object RegistrationQueueListener {
  def props(rabbitControl: ActorRef): Props = Props(new RegistrationQueueListener(rabbitControl))

  val name: String = "RegistrationQueueListener"
  val QUEUE: String = "ReCoEngineRegistry"
}

class RegistrationQueueListener(rabbitControl: ActorRef) extends Actor with ActorLogging with RegistrationJsonSupport with LimitedRedeliveryStrategy {
  import RegistrationQueueListener.QUEUE

  implicit val ec: ExecutionContextExecutor = context.system.dispatcher

  var myQueueSubscription: Option[SubscriptionRef] = None

  override def receive: Receive = {
    case Listen =>
      myQueueSubscription = Some(
        Subscription.run(rabbitControl) {
          channel(qos = 3) {
            consume(Queue.passive(topic(queue(QUEUE), List(s"$QUEUE.#")))) {
              body(as[RegistrationMessage]) {
                obj =>
                  log.debug(s"received my object $obj")
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
