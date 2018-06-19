package hu.elte.inf.recommenderSystems.model

import hu.elte.inf.recommenderSystems.model.enum.Method.Method

package object mychannel {

  sealed trait MyChannelMessage

  case class GetRecommendations(`type`: String = "Person", externalId: Long, maxResults: Int) extends MyChannelMessage

  case class GetRecommendationsMethod(method: Method, task: GetRecommendations) extends MyChannelMessage

  case class ReplyObj(itemCount: Int, itemList: List[Item])

  case class Item(id: Long, `type`: String, score: Double, ratingEstimation: Option[Double] = None, title: String, externalId: Long)

}
