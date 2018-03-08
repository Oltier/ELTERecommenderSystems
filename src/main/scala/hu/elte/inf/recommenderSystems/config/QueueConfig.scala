package hu.elte.inf.recommenderSystems.config

import pureconfig.{CamelCase, ConfigFieldMapping, ProductHint}

object QueueConfig {

  implicit def hint[T]: ProductHint[T] = ProductHint[T](ConfigFieldMapping(CamelCase, CamelCase))

  import pureconfig._

  final case class Queue(name: String)

  lazy val QUEUE: Queue = loadConfigOrThrow[Queue]("queue")

}
