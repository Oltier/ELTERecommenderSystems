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

  implicit object TrueKnowledgeBaseEmitJsonFormat extends RootJsonFormat[TrueKnowledgeBaseEmit] {
    override def write(obj: TrueKnowledgeBaseEmit): JsObject = {
      obj.configName.fold(
        JsObject(
          "channel" -> obj.channel.toJson,
          "configName" -> JsNull,
          "itemCount" -> obj.itemCount.toJson
        )
      )( confName =>
        JsObject(
          "channel" -> obj.channel.toJson,
          "configName" -> confName.toJson,
          "itemCount" -> obj.itemCount.toJson
        )
      )
    }

    override def read(json: JsValue): TrueKnowledgeBaseEmit = json match {
      case JsObject(fields) if fields("channel") != null && fields("configName") != null =>
        json.convertTo[TrueKnowledgeBaseEmit]
      case JsObject(fields) if fields("channel") != null && fields("configName") == null =>
        TrueKnowledgeBaseEmit(channel = fields("channel").convertTo[String], itemCount = fields("itemCount").convertTo[Int])
    }
  }

  implicit val knowledgeBaseEmitMethodJsonFormat: RootJsonFormat[KnowledgeBaseEmitMethod] = jsonFormat2(KnowledgeBaseEmitMethod)
  implicit val trueKnowledgeBaseEmitMethodJsonFormat: RootJsonFormat[TrueKnowledgeBaseEmitMethod] = jsonFormat2(TrueKnowledgeBaseEmitMethod)
}
