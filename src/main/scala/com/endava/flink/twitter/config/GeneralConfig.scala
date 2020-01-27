package com.endava.flink.twitter.config

import java.util.Properties

import cats.effect.IO
import com.endava.flink.twitter.JobException
import com.endava.flink.twitter.sink.MongoSink.MongoSinkConfig
import com.typesafe.config.ConfigFactory
import org.apache.flink.configuration.Configuration

object GeneralConfig {

  def apply(args: Array[String]): GeneralConfig = {
    new GeneralConfig(getPropertiesFromLocal())
  }

  private def getPropertiesFromLocal(): IO[com.typesafe.config.Config] = {
    IO {
      ConfigFactory.load("application.conf")
    }
  }

}

class GeneralConfig(generalConfig: IO[com.typesafe.config.Config]) {

  case class KafkaConfigException(msg: String, exception: Throwable) extends JobException {
    override def getMsgException(): String = msg

    override def getException(): Throwable = exception
  }

  case class FlinkConfigException(msg: String, exception: Throwable) extends JobException {
    override def getMsgException(): String = msg

    override def getException(): Throwable = exception
  }

  case class MongoConfigException(msg: String, exception: Throwable) extends JobException {
    override def getMsgException(): String = msg

    override def getException(): Throwable = exception
  }

  def getFlinkConfig(): IO[Configuration] = {
    generalConfig.flatMap {
      config =>
        IO {
          val metricsConfig = config.getConfig("metrics")
          val flinkConfig = new Configuration()
          flinkConfig.setString("metrics.reporters", metricsConfig.getString("reporters"))
          flinkConfig.setString("metrics.reporter.influxDBReporter.class", metricsConfig.getString("class"))
          flinkConfig.setString("metrics.reporter.influxDBReporter.interval", metricsConfig.getString("interval"))
          flinkConfig.setString("metrics.reporter.influxDBReporter.url", metricsConfig.getString("url"))
          flinkConfig.setString("metrics.reporter.influxDBReporter.username", metricsConfig.getString("username"))
          flinkConfig.setString("metrics.reporter.influxDBReporter.password", metricsConfig.getString("password"))
          flinkConfig.setString("metrics.reporter.influxDBReporter.database", metricsConfig.getString("database"))
          flinkConfig.setString("metrics.reporter.influxDBReporter.filter", metricsConfig.getString("filters"))
          flinkConfig
        }.handleErrorWith {
          error =>
            IO.raiseError(FlinkConfigException("error getting the flink configuration: " + error.getMessage, error))
        }
    }
  }

  def getKafkaConfig(): IO[KafkaConfig] = {
    generalConfig.flatMap {
      config =>
        IO {
          val kafkaConfig = config.getConfig("kafka")
          val properties = new Properties()
          properties.setProperty("bootstrap.servers", kafkaConfig.getString("servers"))
          properties.setProperty("group.id", kafkaConfig.getString("group.id"))
          properties.setProperty("zookeeper.connect", kafkaConfig.getString("zookeeper"))
          KafkaConfig(properties, List(kafkaConfig.getString("topic")))
        }.handleErrorWith {
          error =>
            IO.raiseError(KafkaConfigException("error getting the kafka configuration: " + error.getMessage, error))
        }
    }
  }

  def getMongoConfig():IO[MongoSinkConfig] = {
    generalConfig.flatMap{
      config =>
        IO{
          val mongoConfig = config.getConfig("mongo")
          MongoSinkConfig("",mongoConfig.getString("db"),mongoConfig.getString("collection"))
        }.handleErrorWith {
          error =>
            IO.raiseError(MongoConfigException("error getting the mongo configuration: " + error.getMessage, error))
        }
    }
  }

}
