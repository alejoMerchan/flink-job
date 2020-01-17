package com.endava.flink.twitter

import com.endava.flink.twitter.config.KafkaConfigurations
import com.endava.flink.twitter.sink.mongo.DataMongoSink
import com.endava.flink.twitter.source.KafkaSource
import com.endava.flink.twitter.transform.DataTranslate
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala._

object TwitterProcessor
  extends App
    with DataTranslate
    with KafkaConfigurations
    with KafkaSource
    with DataMongoSink {

    // set up the streaming execution environment
    implicit val env = StreamExecutionEnvironment.getExecutionEnvironment

    /**
      * Event time and watermarks:
      * Event time: The time at which event occurred on its producing device, we need to specify the way to
      * assign watermarks and timestamps.
      * Processing time: Processing time is the time of machine executing the stream of data processing.
      * Ingestion time: This is time at which a particular event enters Flink.
      */
    env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime)

    val process = for {
      kafkaConfig <- getConfig("")
      source <- getSource(kafkaConfig)
      translate <- toTransform(source)
      sink <- sink(translate)
    } yield sink

    process.attempt.map {
      case Left(e1) =>
        e1 match {
          case jobException: JobException => println(jobException.getMsgException())
          case _ => println("other exception")
        }
      case Right(_) => println("success execution")
    }.unsafeRunSync()

    env.execute("twitter processor app")
}