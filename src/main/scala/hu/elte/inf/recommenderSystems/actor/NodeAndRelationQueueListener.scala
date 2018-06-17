package hu.elte.inf.recommenderSystems.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.spingo.op_rabbit.Directives._
import com.spingo.op_rabbit._
import com.spingo.op_rabbit.SprayJsonSupport._
import hu.elte.inf.recommenderSystems.model.entityrelation.{EntityRelationJsonSupport, EntityRelationMessage}

import scala.concurrent.ExecutionContextExecutor

object NodeAndRelationQueueListener {
  def props(rabbitControl: ActorRef): Props = Props(new NodeAndRelationQueueListener(rabbitControl))

  val name: String = "NodeAndRelationQueueListener"
  val QUEUE: String = "KB_ML1M"
}

class NodeAndRelationQueueListener(rabbitControl: ActorRef) extends Actor with ActorLogging with EntityRelationJsonSupport with LimitedDeliveryStrategy {
  import NodeAndRelationQueueListener.QUEUE

  implicit val ec: ExecutionContextExecutor = context.system.dispatcher

  var myQueueSubscription: Option[SubscriptionRef] = None

  override def receive: Receive = {
    case Listen =>
      myQueueSubscription = Some(
        Subscription.run(rabbitControl) {
          channel(qos = 3) {
            consume(Queue.passive(topic(queue(QUEUE), List(s"$QUEUE.#")))) {
              (body(as[EntityRelationMessage]) & routingKey) {
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
