package hu.elte.inf.recommenderSystems.model.registration

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait RegistrationJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val registrationTaskJsonFormat: RootJsonFormat[RegistrationTask] = jsonFormat4(RegistrationTask.apply)
  implicit val registrationMessageJsonFormat: RootJsonFormat[RegistrationMessage] = jsonFormat2(RegistrationMessage)

}
