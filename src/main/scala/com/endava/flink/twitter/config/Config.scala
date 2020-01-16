package com.endava.flink.twitter.config

import cats.effect.IO

trait Config[A, B] {

  def getConfig(in:A): IO[B]

}
