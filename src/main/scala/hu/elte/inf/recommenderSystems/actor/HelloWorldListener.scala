package hu.elte.inf.recommenderSystems.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.spingo.op_rabbit.Directives._
import com.spingo.op_rabbit._
import com.spingo.op_rabbit.properties.{CorrelationId, ReplyTo}
import spray.json._
import hu.elte.inf.recommenderSystems.actor.MessageSender.{SendJsonMessage, SendMessageWithCorrelationId, SendMessageWithCorrelationIdHelloWord}
import hu.elte.inf.recommenderSystems.model.entityrelation.EntityRelationMessage

import scala.concurrent.ExecutionContextExecutor

object HelloWorldListener {
  def props(rabbitControl: ActorRef, messageSender: ActorRef): Props = Props(new HelloWorldListener(rabbitControl, messageSender))

  val name: String = "HelloWorldQueueListener"
  val QUEUE: String = "HelloWorld"
}

class HelloWorldListener(rabbitControl: ActorRef, messageSender: ActorRef) extends Actor with ActorLogging with LimitedRedeliveryStrategy {
  import HelloWorldListener.QUEUE

  implicit val ec: ExecutionContextExecutor = context.system.dispatcher

  var myQueueSubscription: Option[SubscriptionRef] = None

  override def receive: Receive = {
    case Listen =>
      myQueueSubscription = Some(
        Subscription.run(rabbitControl) {
          channel(qos = 3) {
            consume(Queue.passive(topic(queue(QUEUE), List(s"$QUEUE.#")))) {
              (body(as[String]) & optionalProperty(ReplyTo) & optionalProperty(CorrelationId)) {
                (name, replyTo, correlationId) =>
                  log.debug(s"""received my object $name with replyTo channel: ${replyTo.getOrElse("No channel")} and correlationId: ${correlationId.getOrElse("No correlation Id")}""")
                  if(replyTo.isDefined && correlationId.isDefined) {
                    messageSender ! SendMessageWithCorrelationIdHelloWord(replyTo.get, s"Hello $name", correlationId.get)
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
