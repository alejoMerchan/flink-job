package com.endava.flink.twitter.source

import cats.effect.IO
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

trait Source[A, B] {

  def getSource(in:A )(implicit env: StreamExecutionEnvironment ): IO[B]

}
