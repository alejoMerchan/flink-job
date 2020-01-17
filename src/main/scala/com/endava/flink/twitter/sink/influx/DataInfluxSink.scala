package com.endava.flink.twitter.sink.influx

import cats.effect.IO
import com.endava.flink.twitter.JobException
import com.endava.flink.twitter.sink.DataSink
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.scala.DataStream


trait DataInfluxSink extends DataSink[InfluxDBPoint, DataStreamSink[InfluxDBPoint]] {

  final case class DataInfluxSinkException(msg: String, exception: Throwable) extends JobException {

    def getMsgException(): String = msg

    def getException(): Throwable = exception
  }

  override def sink(stream: DataStream[InfluxDBPoint]): IO[DataStreamSink[InfluxDBPoint]] = {
    IO {
      stream.addSink(InfluxSink(InfluxSinkConfig("localhost", "", "", "influx_database"))).setParallelism(1)
    }.handleErrorWith { error =>
      IO.raiseError(DataInfluxSinkException(s"There was an error executing InfluxSink: ${error.getMessage}", error))
    }
  }
}