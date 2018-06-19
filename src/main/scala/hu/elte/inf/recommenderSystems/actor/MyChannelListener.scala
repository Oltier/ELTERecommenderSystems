package hu.elte.inf.recommenderSystems.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.spingo.op_rabbit.Directives._
import com.spingo.op_rabbit.SprayJsonSupport._
import com.spingo.op_rabbit._
import com.spingo.op_rabbit.properties.{CorrelationId, ReplyTo}
import hu.elte.inf.recommenderSystems.actor.MessageSender.SendMessageWithCorrelationId
import hu.elte.inf.recommenderSystems.config.Config
import hu.elte.inf.recommenderSystems.model.mychannel.{GetRecommendationsMethod, Item, MyChannelJsonSupport, ReplyObj}
import spray.json._

import scala.concurrent.ExecutionContextExecutor

object MyChannelListener {

  def props(rabbitControl: ActorRef, messageSender: ActorRef): Props = Props(new MyChannelListener(rabbitControl, messageSender))

  val name: String = "MyChannelQueueListener"
  val QUEUE: String = Config.QUEUE.myChannel

}

class MyChannelListener(rabbitControl: ActorRef, messageSender: ActorRef) extends Actor with ActorLogging with MyChannelJsonSupport with LimitedRedeliveryStrategy {
  import MyChannelListener.QUEUE

  implicit val ec: ExecutionContextExecutor = context.system.dispatcher

  var myQueueSubscription: Option[SubscriptionRef] = None

  override def receive: Receive = {
    case Listen =>
      myQueueSubscription = Some(
        Subscription.run(rabbitControl) {
          channel(qos = 3) {
            consume(Queue.passive(topic(queue(QUEUE), List(s"$QUEUE.#")))) {
              (body(as[GetRecommendationsMethod])& optionalProperty(ReplyTo) & optionalProperty(CorrelationId)) {
                (obj, replyTo, correlationId) =>
                  log.debug(s"received json \n $obj")
                  if(replyTo.isDefined && correlationId.isDefined) {
                    val replyMessage = ReplyObj(5, List(Item(10, "Movie", 3.2, None, "Hello world", 123)))
                    messageSender ! SendMessageWithCorrelationId(replyTo.get, replyMessage.toJson, correlationId.get)
                  }
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
