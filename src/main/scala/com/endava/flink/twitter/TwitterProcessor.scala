package com.endava.flink.twitter

import com.endava.flink.twitter.config.{GeneralConfig, KafkaConfigurations}
import com.endava.flink.twitter.sink.DataMongoSink
import com.endava.flink.twitter.source.KafkaSource
import com.endava.flink.twitter.transform.DataTranslate
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala._

object TwitterProcessor extends DataTranslate with KafkaConfigurations with KafkaSource with DataMongoSink {


  def main(args: Array[String]): Unit = {

    val generalConfig = GeneralConfig(args)

    implicit val env = StreamExecutionEnvironment.getExecutionEnvironment

    for {
      flinkConfig <- generalConfig.getFlinkConfig()
    } yield env.configure(flinkConfig,classOf[Object].getClassLoader)

    env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime)
    val flow = for {
      kafkaConfig <- generalConfig.getKafkaConfig()
      source <- getSource(kafkaConfig)
      translate <- toTransform(source)
      mongoConfig <- generalConfig.getMongoConfig()
      sink <- sink(translate, mongoConfig)
    } yield sink
    val result = flow.unsafeRunSync()
    env.execute("twitter processor app")
  }

}
