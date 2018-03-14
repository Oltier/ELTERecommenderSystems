package hu.elte.inf.recommenderSystems

import akka.actor.ActorSystem
import hu.elte.inf.recommenderSystems.actor.Supervisor
import hu.elte.inf.recommenderSystems.actor.Supervisor.{Begin, End, SendMessage}
import hu.elte.inf.recommenderSystems.config.Config
import hu.elte.inf.recommenderSystems.model.{Method, RegistrationMessage, Task}
import play.api.libs.json.Json

object Main extends App {

  val system: ActorSystem = ActorSystem("rabbitMq")

  val supervisor = system.actorOf(Supervisor.props)
  supervisor ! Begin

  if (Config.SETUP.enableSendRegisterMessage) {

    val registrationMessage = RegistrationMessage(Method.REGISTER, Task("tudlik_zoltan_ce0ta3", Config.DEVELOPER))

    supervisor ! SendMessage(Config.QUEUE.name, Json.toJson(registrationMessage))

  }

  scala.io.StdIn.readLine("Press any key to stop the system\n")

  supervisor ! End

  system.terminate()

}
