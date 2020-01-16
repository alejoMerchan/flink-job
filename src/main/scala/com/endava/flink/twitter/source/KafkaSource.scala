package com.endava.flink.twitter.source

import cats.effect.IO
import com.endava.flink.twitter.JobException
import com.endava.flink.twitter.config.KafkaConfig
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, _}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer

trait KafkaSource extends Source[KafkaConfig, DataStream[String]] {

  case class KafkaSourceException(msg: String, exception: Throwable) extends JobException {
    override def getMsgException(): String = msg

    override def getException(): Throwable = exception
  }

  override def getSource(in: KafkaConfig)(implicit env: StreamExecutionEnvironment): IO[DataStream[String]] = {
    IO {
      env.addSource(new FlinkKafkaConsumer[String](in.topics.head,
        new SimpleStringSchema(), in.properties))
    }.handleErrorWith {
      error =>
        IO.raiseError(KafkaSourceException("error creating the kafka source", error))
    }
  }
}
