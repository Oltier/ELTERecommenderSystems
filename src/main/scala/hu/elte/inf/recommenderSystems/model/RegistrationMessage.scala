package hu.elte.inf.recommenderSystems.model

import hu.elte.inf.recommenderSystems.model.Method.Method
import play.api.libs.json.{Json, OFormat}

case class RegistrationMessage(method: Method, task: Task)

object RegistrationMessage {
  implicit def myObjectFormat: OFormat[RegistrationMessage] = Json.format[RegistrationMessage]
}