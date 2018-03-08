package hu.elte.inf.recommenderSystems

import akka.actor.ActorSystem
import hu.elte.inf.recommenderSystems.actor.Supervisor
import hu.elte.inf.recommenderSystems.actor.Supervisor.{Begin, End, SendMessage}
import hu.elte.inf.recommenderSystems.model.MyObject

import scala.concurrent.ExecutionContextExecutor

object Main extends App {

  val system: ActorSystem = ActorSystem("rabbitMq")

  val supervisor = system.actorOf(Supervisor.props)
  supervisor ! Begin
  supervisor ! SendMessage(MyObject("MZ/X jelentkezz!"))

  val message = scala.io.StdIn.readLine("What's the message?\n")
  supervisor ! SendMessage(MyObject(message))

  scala.io.StdIn.readLine("Press any key to stop the system\n")

  supervisor ! End

  system.terminate()

}
