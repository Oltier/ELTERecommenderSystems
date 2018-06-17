package hu.elte.inf.recommenderSystems.config

import pureconfig.{CamelCase, ConfigFieldMapping, ProductHint}

object Config {

  implicit def hint[T]: ProductHint[T] = ProductHint[T](ConfigFieldMapping(CamelCase, CamelCase))

  import pureconfig._

  final case class Queue(name: String)
  final case class Developer(developerEmail: String, developerName: String, developerNeptun: String)
  final case class Setup(enableSendRegisterMessage: Boolean)

  lazy val QUEUE: Queue = loadConfigOrThrow[Queue]("queue")
  lazy val DEVELOPER: Developer = loadConfigOrThrow[Developer]("developer")
  lazy val SETUP: Setup = loadConfigOrThrow[Setup]("setup")

}