name := "RecommenderSystems"

version := "0.1"

scalaVersion := "2.12.4"

val opRabbitVersion = "2.1.0"
val akkaVersion = "2.5.11"

val akkaDependencies = Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
)

val mqDependencies = Seq(
  "com.spingo" %% "op-rabbit-core" % opRabbitVersion,
  "com.spingo" %% "op-rabbit-spray-json" % opRabbitVersion,
  "com.spingo" %% "op-rabbit-json4s" % opRabbitVersion,
  "com.spingo" %% "op-rabbit-airbrake" % opRabbitVersion,
  "com.spingo" %% "op-rabbit-akka-stream" % opRabbitVersion
)

val configDependencies = Seq(
  "com.github.pureconfig" %% "pureconfig" % "0.8.0",
  "org.slf4j" % "slf4j-simple" % "1.7.25",
  "org.scaldi" %% "scaldi" % "0.5.8"
)

val otherDependencies = Seq(
  "org.json4s" %% "json4s-native" % "3.5.3",
  "io.spray" %%  "spray-json" % "1.3.3",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.3",
  "org.scalatest" % "scalatest_2.12" % "3.0.4",
  "joda-time" % "joda-time" % "2.9.9"
)

libraryDependencies ++=
  mqDependencies ++
    configDependencies ++
    otherDependencies