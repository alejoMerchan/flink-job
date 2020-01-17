package com.endava.flink.twitter.sink.influx

import com.paulgoldbaum.influxdbclient.Parameter.{Consistency, Precision}
import com.paulgoldbaum.influxdbclient._
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}

import scala.concurrent.ExecutionContext.Implicits.global

class InfluxSink(config: InfluxSinkConfig) extends RichSinkFunction[InfluxDBPoint] {

  var influxClient: InfluxDB = _

  override def open(parameters: Configuration): Unit = {
    influxClient = InfluxDB.connect(config.url, 8086)
  }

  override def invoke(event: InfluxDBPoint, context: SinkFunction.Context[_]): Unit = {

    val database = influxClient.selectDatabase(config.database)

    val point = Point(event.measurement, event.timestamp)

    database.write(point,
      precision = Precision.MILLISECONDS,
      consistency = Consistency.ALL,
      retentionPolicy = "custom_rp"
    ).recover { case e: WriteException =>
      println(s"There was an error inserting the data. ${e.getMessage}")
    }
  }

  override def close(): Unit = influxClient.close()
}

object InfluxSink {
  def apply(config: InfluxSinkConfig): InfluxSink = new InfluxSink(config)
}