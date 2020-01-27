package com.endava.flink.twitter

import cats.effect.IO
import com.endava.flink.twitter.config.{GeneralConfig, KafkaConfigurations}
import com.endava.flink.twitter.sink.DataMongoSink
import com.endava.flink.twitter.source.KafkaSource
import com.endava.flink.twitter.transform.DataTranslate
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.environment.LocalStreamEnvironment
import org.apache.flink.streaming.api.scala._

object TwitterProcessor extends DataTranslate with KafkaConfigurations with KafkaSource with DataMongoSink {


  def main(args: Array[String]): Unit = {

    val generalConfig = GeneralConfig(args)
    val executeJob = for {
      flinkConfig <- generalConfig.getFlinkConfig()
      process <- {
        IO {
          implicit val env = new StreamExecutionEnvironment(new LocalStreamEnvironment(flinkConfig))
          env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime)
          val flow = for {
            kafkaConfig <- generalConfig.getKafkaConfig()
            source <- getSource(kafkaConfig)
            translate <- toTransform(source)
            sink <- sink(translate)
          } yield sink
          val result = flow.unsafeRunSync()
          env.execute("twitter processor app")
          result
        }
      }
    } yield process


    executeJob.attempt.map {
      case Left(e1) =>
        e1 match {
          case jobException: JobException => println(jobException.getMsgException())
          case _ =>
            println("other exception")
            e1.printStackTrace()
        }
      case Right(_) => println("sucess execution")
    }.unsafeRunSync()

  }


}
