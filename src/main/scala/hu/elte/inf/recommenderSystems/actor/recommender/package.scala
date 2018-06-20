package hu.elte.inf.recommenderSystems.actor

package object recommender {

  final case class Recommendation(rank: Int, itemId: Int, title: String, rating: Double) {
    override def toString: String = s"\n$rank. $title (id: $itemId) $rating"
  }

  final case class Recommendations(items: List[Recommendation])

}
