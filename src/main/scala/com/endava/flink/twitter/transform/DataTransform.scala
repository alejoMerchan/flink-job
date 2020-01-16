package com.endava.flink.twitter.transform

import cats.effect.IO
import org.apache.flink.streaming.api.scala.DataStream

trait DataTransform[A, B] {

  def toTransform(stream: DataStream[A]): IO[DataStream[B]]

}
