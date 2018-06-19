package hu.elte.inf.recommenderSystems.model.knowledgebase

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

trait KnowledgeBaseJsonSupport extends DefaultJsonProtocol with SprayJsonSupport{
  implicit val knowledgeEmitMethodJsonFormat: RootJsonFormat[KnowledgeBaseEmit] = jsonFormat2(KnowledgeBaseEmit)
  implicit val knowledgeBaseEmitMethodJsonFormat: RootJsonFormat[KnowledgeBaseEmitMethod] = jsonFormat2(KnowledgeBaseEmitMethod)
  implicit val trueKnowledgeBaseEmitJsonFormat: RootJsonFormat[TrueKnowledgeBaseEmit] = jsonFormat3(TrueKnowledgeBaseEmit)
  implicit val trueKnowledgeBaseEmitMethodJsonFormat: RootJsonFormat[TrueKnowledgeBaseEmitMethod] = jsonFormat2(TrueKnowledgeBaseEmitMethod)
}
