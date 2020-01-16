package com.endava.flink.twitter.sink

import cats.effect.IO
import org.apache.flink.streaming.api.scala.DataStream

trait DataSink[A, B] {

  def sink(stream:DataStream[A]):IO[B]

}
