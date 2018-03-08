package hu.elte.inf.recommenderSystems.model

import play.api.libs.json.{Json, OFormat}

case class MyObject(message: String)

object MyObject {
  implicit def myObjectFormat: OFormat[MyObject] = Json.format[MyObject]
}