package hu.elte.inf.recommenderSystems.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.spingo.op_rabbit.Directives._
import com.spingo.op_rabbit.SprayJsonSupport._
import com.spingo.op_rabbit._
import hu.elte.inf.recommenderSystems.config.Config
import hu.elte.inf.recommenderSystems.model.mychannel.{GetRecommendationsMethod, MyChannelJsonSupport}
import hu.elte.inf.recommenderSystems.model.registration.{RegistrationJsonSupport, RegistrationMessage}
import spray.json.JsValue

import scala.concurrent.ExecutionContextExecutor

object MyChannelListener {

  def props(rabbitControl: ActorRef): Props = Props(new MyChannelListener(rabbitControl))

  val name: String = "MyChannelQueueListener"
  val QUEUE: String = Config.QUEUE.myChannel

}

class MyChannelListener(rabbitControl: ActorRef) extends Actor with ActorLogging with MyChannelJsonSupport with LimitedRedeliveryStrategy {
  import MyChannelListener.QUEUE

  implicit val ec: ExecutionContextExecutor = context.system.dispatcher

  var myQueueSubscription: Option[SubscriptionRef] = None

  override def receive: Receive = {
    case Listen =>
      myQueueSubscription = Some(
        Subscription.run(rabbitControl) {
          channel(qos = 3) {
            consume(Queue.passive(topic(queue(QUEUE), List(s"$QUEUE.#")))) {
              body(as[GetRecommendationsMethod]) {
                obj =>
                  log.debug(s"received json \n $obj")
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
