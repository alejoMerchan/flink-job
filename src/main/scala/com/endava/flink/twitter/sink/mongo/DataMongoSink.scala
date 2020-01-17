package com.endava.flink.twitter.sink.mongo

import cats.effect.IO
import com.endava.flink.twitter.JobException
import com.endava.flink.twitter.model.TwitterEvent
import com.endava.flink.twitter.sink.DataSink
import com.endava.flink.twitter.sink.mongo.MongoSink.MongoSinkConfig
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.scala.DataStream

trait DataMongoSink extends DataSink[TwitterEvent, DataStreamSink[TwitterEvent]] {

  case class DataMongoSinkException(msg: String, exception: Throwable) extends JobException {
    override def getMsgException(): String = msg

    override def getException(): Throwable = exception
  }

  override def sink(stream: DataStream[TwitterEvent]): IO[DataStreamSink[TwitterEvent]] = {
    IO {
      stream.addSink(MongoSink(MongoSinkConfig("", "twitter-endava", "tweets"))).setParallelism(1)
    }.handleErrorWith {
      error =>
        IO.raiseError(DataMongoSinkException("error executing the mongo sink: " + error.getMessage, error))
    }
  }

}
