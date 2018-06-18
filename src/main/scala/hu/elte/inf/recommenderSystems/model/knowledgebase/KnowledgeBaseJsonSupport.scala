package hu.elte.inf.recommenderSystems.model.knowledgebase

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

trait KnowledgeBaseJsonSupport extends DefaultJsonProtocol with SprayJsonSupport{

  implicit object KnowledgeBaseEmitJsonFormat extends RootJsonFormat[KnowledgeBaseEmit] {
    override def write(obj: KnowledgeBaseEmit): JsObject = {
      obj.configName.fold(
        JsObject(
          "channel" -> obj.channel.toJson,
          "configName" -> JsNull
        )
      )( confName =>
        JsObject(
          "channel" -> obj.channel.toJson,
          "configName" -> confName.toJson
        )
      )
    }

    override def read(json: JsValue): KnowledgeBaseEmit = json match {
      case JsObject(fields) if fields("channel") != null && fields("configName") != null =>
        json.convertTo[KnowledgeBaseEmit]
      case JsObject(fields) if fields("channel") != null && fields("configName") == null =>
        KnowledgeBaseEmit(fields("channel").convertTo[String])
    }
  }

  implicit val knowledgeBaseEmitMethodJsonFormat: RootJsonFormat[KnowledgeBaseEmitMethod] = jsonFormat2(KnowledgeBaseEmitMethod)
}
