package hu.elte.inf.recommenderSystems.model.mychannel

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._


trait MyChannelJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val getRecommendationsJsonFormat: RootJsonFormat[GetRecommendations] = jsonFormat3(GetRecommendations)
  implicit val getRecommendationsMethodJsonFormat: RootJsonFormat[GetRecommendationsMethod] = jsonFormat2(GetRecommendationsMethod)
  implicit val itemJsonFormat: RootJsonFormat[Item] = jsonFormat6(Item)
  implicit val replyObjJsonFormat: RootJsonFormat[ReplyObj] = jsonFormat2(ReplyObj)
}
