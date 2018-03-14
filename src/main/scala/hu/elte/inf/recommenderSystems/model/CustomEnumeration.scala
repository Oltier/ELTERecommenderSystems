package hu.elte.inf.recommenderSystems.model

import spray.json.{DeserializationException, JsString, JsValue, JsonFormat}

trait CustomEnumeration extends Enumeration {
  type EnumType = Value
}

//trait PersistableEnumeration { customEnum: CustomEnumeration =>
//  import com.wanari.mlp.DatabaseImplicits.implicitJdbcProfile.api._
//
//  implicit val customEnumerationMapper: JdbcType[customEnum.EnumType] with BaseTypedType[customEnum.EnumType] =
//    MappedColumnType.base[customEnum.EnumType, String](
//      e => e.toString,
//      (s: String) => withName(s)
//    )
//}

trait SerializableEnumeration { customEnum: CustomEnumeration =>
  implicit object CustomEnumerationJsonFormat extends JsonFormat[customEnum.EnumType] {
    def write(obj: customEnum.EnumType): JsValue = JsString(obj.toString)

    def read(json: JsValue): customEnum.EnumType = json match {
      case JsString(str) => withName(str)
      case _ => throw DeserializationException("Enum string expected")
    }
  }
}
