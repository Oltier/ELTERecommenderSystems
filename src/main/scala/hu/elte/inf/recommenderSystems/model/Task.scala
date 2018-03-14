package hu.elte.inf.recommenderSystems.model

import hu.elte.inf.recommenderSystems.config.Config.Developer
import play.api.libs.json.{Json, OFormat}

case class Task(methodName: String, developerEmail: String, developerName: String, developerNeptun: String)

object Task {
  def apply(methodName: String, developer: Developer): Task = {
    new Task(
      methodName,
      developer.developerEmail,
      developer.developerName,
      developer.developerNeptun)
  }

  implicit def taskFormat: OFormat[Task] = Json.format[Task]
}
