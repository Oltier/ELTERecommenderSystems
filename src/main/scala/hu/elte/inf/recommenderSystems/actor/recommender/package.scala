package hu.elte.inf.recommenderSystems.actor

package object recommender {

  final case class Recommendation(itemId: Int, rating: Double)
  final case class Recommendations(items: List[Recommendation])

}
