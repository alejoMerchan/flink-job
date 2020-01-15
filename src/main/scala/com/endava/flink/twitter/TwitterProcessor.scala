package com.endava.flink.twitter

import java.util.Properties

import com.endava.flink.twitter.function.TwitterTranslate
import com.endava.flink.twitter.sink.MongoSink
import com.endava.flink.twitter.sink.MongoSink.MongoSinkConfig
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer

object TwitterProcessor {


  def main(args: Array[String]): Unit = {

    // set up the streaming execution environment
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    /**
      * Event time and watermarks:
      * Event time: The time at which event occurred on its producing device, we need to specify the way to
      * assign watermarks and timestamps.
      * Processing time: Processing time is the time of machine executing the stream of data processing.
      * Ingestion time: This is time at which a particular event enters Flink.
      */
    env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime)

    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "localhost:9092")
    properties.setProperty("group.id", "test")
    properties.setProperty("zookeeper.connect", "localhost:2181")


    val kafkaData: DataStream[String] = env.addSource(new FlinkKafkaConsumer[String]("eventos-prueba", new SimpleStringSchema(), properties))

    kafkaData.map(new TwitterTranslate).addSink(MongoSink(MongoSinkConfig("", "twitter-endava", "tweets"))).setParallelism(1)

    env.execute("twitter processor app")

  }


}
