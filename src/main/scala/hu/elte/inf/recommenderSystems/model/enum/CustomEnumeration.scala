package hu.elte.inf.recommenderSystems.model.enum

import spray.json.{DeserializationException, JsString, JsValue, JsonFormat}

trait CustomEnumeration extends Enumeration {
  type EnumType = Value
}

trait SerializableEnumeration { customEnum: CustomEnumeration =>
  implicit object CustomEnumerationJsonFormat extends JsonFormat[customEnum.EnumType] {
    override def read(json: JsValue): customEnum.EnumType = json match {
      case JsString(str) => withName(str)
      case _ => throw DeserializationException("Enum string expected")
    }

    override def write(obj: customEnum.EnumType): JsValue = JsString(obj.toString)
  }
}
