package hu.elte.inf.recommenderSystems.model.entityrelation

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import hu.elte.inf.recommenderSystems.model.enum.Method
import hu.elte.inf.recommenderSystems.model.enum.Method.Method
import spray.json.{DefaultJsonProtocol, DeserializationException, JsObject, JsValue, RootJsonFormat, RootJsonReader}

trait EntityRelationJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val addNodeTypeJsonFormat: RootJsonFormat[AddNodeType] = jsonFormat1(AddNodeType)
  implicit val addNodeTypeMethodJsonFormat: RootJsonFormat[AddNodeTypeMethod] = jsonFormat2(AddNodeTypeMethod)
  implicit val addRelationTypeJsonFormat: RootJsonFormat[AddRelationType] = jsonFormat3(AddRelationType)
  implicit val addRelationTypeMethodJsonFormat: RootJsonFormat[AddRelationTypeMethod] = jsonFormat2(AddRelationTypeMethod)

  implicit object EntityRelationMessageFormat extends RootJsonReader[EntityRelationMessage] {
    override def read(json: JsValue): EntityRelationMessage = json match {
      case JsObject(fields) if fields.keys.exists(_ == "title") && fields("title").convertTo[Method] == Method.ADD_NODE_TYPE =>
        json.convertTo[AddNodeTypeMethod]
      case JsObject(fields) if fields.keys.exists(_ == "title") && fields("title").convertTo[Method] == Method.ADD_RELATION_TYPE =>
        json.convertTo[AddRelationTypeMethod]
      case other => throw DeserializationException(s"Entity relation message expected, but we get $other")
    }
  }

}
