package hu.elte.inf.recommenderSystems.model

import java.nio.file.Paths

import hu.elte.inf.recommenderSystems.config.SparkConfig
import org.apache.spark.mllib.recommendation.Rating
import org.apache.spark.rdd.RDD

class MovieLensData(directoryPath: String) {
  require(directoryPath.nonEmpty)

  def loadMovies(): Map[Int, String] = {
    SparkConfig.sc
      .textFile(s"$directoryPath/movies.csv")
      .filter(!_.contains("movieId"))
      .map { line =>
        val fields = line.split(',')
        (fields(0).toInt, s"${fields(2)} (${fields(1)})")
      }
      .collect
      .toMap
  }

  def loadRatings(): RDD[(Long, Rating)] = {
    SparkConfig.sc
      .textFile(s"$directoryPath/ratings.csv")
      .filter(!_.contains("movieId"))
      .map { line =>
        val fields = line.split(',')
        (fields(3).toLong % 10,
         Rating(fields(0).toInt, fields(1).toInt, fields(2).toDouble))
      }
  }

  def isValid(movies: Map[Int, String], ratings: RDD[(Long, Rating)]): Boolean =
    ratings
      .filter(
        timeStampAndRating =>
          timeStampAndRating._2.rating < 0 ||
            timeStampAndRating._2.rating > 5 ||
            !movies.contains(timeStampAndRating._2.product))
      .count() == 0

}
