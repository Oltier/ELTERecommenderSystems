package hu.elte.inf.recommenderSystems.model.entityrelation

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import hu.elte.inf.recommenderSystems.model.SerializationException
import hu.elte.inf.recommenderSystems.model.enum.Method
import hu.elte.inf.recommenderSystems.model.enum.Method.Method
import spray.json._


trait EntityRelationJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val addNodeTypeJsonFormat: RootJsonFormat[AddNodeType] = jsonFormat1(AddNodeType)
  implicit val addNodeTypeMethodJsonFormat: RootJsonFormat[AddNodeTypeMethod] = jsonFormat2(AddNodeTypeMethod)
  implicit val addRelationTypeJsonFormat: RootJsonFormat[AddRelationType] = jsonFormat3(AddRelationType)
  implicit val addRelationTypeMethodJsonFormat: RootJsonFormat[AddRelationTypeMethod] = jsonFormat2(AddRelationTypeMethod)
  implicit val deleteNodeTypeJsonFormat: RootJsonFormat[DeleteNodeType] = jsonFormat1(DeleteNodeType)
  implicit val deleteNodeTypeMethodJsonFormat: RootJsonFormat[DeleteNodeTypeMethod] = jsonFormat2(DeleteNodeTypeMethod)
  implicit val deleteRelationTypeJsonFormat: RootJsonFormat[DeleteRelationType] = jsonFormat1(DeleteRelationType)
  implicit val deleteRelationTypeMethodJsonFormat: RootJsonFormat[DeleteRelationTypeMethod] = jsonFormat2(DeleteRelationTypeMethod)
  
  
  implicit object EntityRelationMessageFormat extends RootJsonFormat[EntityRelationMessage] {
    override def read(json: JsValue): EntityRelationMessage = json match {
      case JsObject(fields) if fields.keys.exists(_ == "method") && fields("method").convertTo[Method] == Method.ADD_NODE_TYPE =>
        json.convertTo[AddNodeTypeMethod]
      case JsObject(fields) if fields.keys.exists(_ == "method") && fields("method").convertTo[Method] == Method.ADD_RELATION_TYPE =>
        json.convertTo[AddRelationTypeMethod]
      case JsObject(fields) if fields.keys.exists(_ == "method") && fields("method").convertTo[Method] == Method.DELETE_NODE_TYPE =>
        json.convertTo[DeleteNodeTypeMethod]
      case JsObject(fields) if fields.keys.exists(_ == "method") && fields("method").convertTo[Method] == Method.DELETE_RELATION_TYPE =>
        json.convertTo[DeleteRelationTypeMethod]
      case other => throw DeserializationException(s"Entity relation message expected, but we got $other")
    }

    override def write(obj: EntityRelationMessage): JsObject = {
      obj match {
        case o: AddNodeTypeMethod => JsObject(
          "title" -> o.method.toJson,
          "task" -> o.task.toJson
        )

        case o: AddRelationTypeMethod => JsObject(
          "title" -> o.method.toJson,
          "task" -> o.task.toJson
        )

        case o: DeleteNodeTypeMethod => JsObject(
          "title" -> o.method.toJson,
          "task" -> o.task.toJson
        )

        case o: DeleteRelationTypeMethod => JsObject(
          "title" -> o.method.toJson,
          "task" -> o.task.toJson
        )

        case other => throw SerializationException(s"Could not write object $other")
      }
    }
  }

}
