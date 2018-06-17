package hu.elte.inf.recommenderSystems

import akka.actor.ActorSystem
import hu.elte.inf.recommenderSystems.actor.MessageSender.SendMessage
import spray.json._
import hu.elte.inf.recommenderSystems.actor.Supervisor
import hu.elte.inf.recommenderSystems.actor.Supervisor.{Begin, End}
import hu.elte.inf.recommenderSystems.config.Config
import hu.elte.inf.recommenderSystems.model.enum.Method
import hu.elte.inf.recommenderSystems.model.registration.{RegistrationJsonSupport, RegistrationMessage, RegistrationTask}

object Main extends App with RegistrationJsonSupport {

  val system: ActorSystem = ActorSystem("rabbitMq")

  val supervisor = system.actorOf(Supervisor.props)
  supervisor ! Begin

  if (Config.SETUP.enableSendRegisterMessage) {

    val registrationMessage = RegistrationMessage(Method.REGISTER, RegistrationTask("tudlik_zoltan_ce0ta3", Config.DEVELOPER))

    supervisor ! SendMessage(Config.QUEUE.name, registrationMessage.toJson)

  }

  scala.io.StdIn.readLine("Press any key to stop the system\n")

  supervisor ! End

  system.terminate()

}
