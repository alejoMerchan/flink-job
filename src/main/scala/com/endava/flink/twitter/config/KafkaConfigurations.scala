package com.endava.flink.twitter.config

import java.util.Properties

import cats.effect.IO
import com.endava.flink.twitter.JobException

trait KafkaConfigurations extends Config[String, KafkaConfig] {

  case class KafkaConfigException(msg: String, exception: Throwable) extends JobException {
    override def getMsgException(): String = msg

    override def getException(): Throwable = exception
  }

  override def getConfig(in: String): IO[KafkaConfig] = {
    IO {
      val properties = new Properties()
      properties.setProperty("bootstrap.servers", "localhost:9092")
      properties.setProperty("group.id", "test")
      properties.setProperty("zookeeper.connect", "localhost:2181")
      KafkaConfig(properties, List("eventos-prueba"))
    }.handleErrorWith {
      error =>
        IO.raiseError(KafkaConfigException("error getting the kafka configuration: " + error.getMessage, error))
    }
  }

}
