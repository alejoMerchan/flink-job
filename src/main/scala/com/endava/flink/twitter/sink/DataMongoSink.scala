package com.endava.flink.twitter.sink

import cats.effect.IO
import com.endava.flink.twitter.JobException
import com.endava.flink.twitter.model.TwitterEvent
import com.endava.flink.twitter.sink.MongoSink.MongoSinkConfig
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.scala._

trait DataMongoSink extends DataSink[TwitterEvent, DataStreamSink[TwitterEvent], MongoSinkConfig] {

  case class DataMongoSinkException(msg: String, exception: Throwable) extends JobException {
    override def getMsgException(): String = msg

    override def getException(): Throwable = exception
  }

  override def sink(stream: DataStream[TwitterEvent],config:MongoSinkConfig): IO[DataStreamSink[TwitterEvent]] = {
    IO {
      stream.addSink(MongoSink(config))
    }.handleErrorWith {
      error =>
        IO.raiseError(DataMongoSinkException("error executing the mongo sink: " + error.getMessage, error))
    }
  }

}
