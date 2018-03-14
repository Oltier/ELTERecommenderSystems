package hu.elte.inf.recommenderSystems.model

import hu.elte.inf.recommenderSystems.model
import play.api.libs.json._

object Method extends Enumeration {
  type Method = Value
  val REGISTER: Value = Value("register")

  implicit val methodReads: Reads[model.Method.Value] = Reads.enumNameReads(Method)
  implicit val methodWrites: Writes[model.Method.Value] = Writes.enumNameWrites
}