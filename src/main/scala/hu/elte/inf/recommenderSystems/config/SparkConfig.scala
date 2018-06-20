package hu.elte.inf.recommenderSystems.config

import org.apache.spark.{SparkConf, SparkContext}

object SparkConfig {

  val config: SparkConf = new SparkConf()
    .setMaster(Config.SPARK.master)
    .setAppName(Config.SPARK.appName)
    .set("spark.executor.memory", "2g")

  val sc = new SparkContext(config)
}
