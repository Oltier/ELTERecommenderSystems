package hu.elte.inf.recommenderSystems.model.mychannel

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import hu.elte.inf.recommenderSystems.model.SerializationException
import hu.elte.inf.recommenderSystems.model.enum.Method
import hu.elte.inf.recommenderSystems.model.enum.Method.Method
import spray.json._


trait MyChannelJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val getRecommendationsJsonFormat: RootJsonFormat[GetRecommendations] = jsonFormat3(GetRecommendations)
  implicit val getRecommendationsMethodJsonFormat: RootJsonFormat[GetRecommendationsMethod] = jsonFormat2(GetRecommendationsMethod)
}
