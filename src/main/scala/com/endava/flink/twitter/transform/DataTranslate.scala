package com.endava.flink.twitter.transform

import cats.effect.IO
import com.endava.flink.twitter.JobException
import com.endava.flink.twitter.model.TwitterEvent
import org.apache.flink.streaming.api.scala._

trait DataTranslate extends DataTransform[String, TwitterEvent] {

  case class DataTranslateException(msg: String, exception: Throwable) extends JobException {
    override def getMsgException(): String = msg

    override def getException(): Throwable = exception
  }

  override def toTransform(stream: DataStream[String]): IO[DataStream[TwitterEvent]] = {
    IO {
      stream.map(new TwitterTranslate)
    }.handleErrorWith {
      error =>
        IO.raiseError(DataTranslateException("error doing the data translation: " + error.getMessage, error))
    }
  }

}
