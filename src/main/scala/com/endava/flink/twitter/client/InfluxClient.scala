package com.endava.flink.twitter.client

import com.paulgoldbaum.influxdbclient.Parameter.{Consistency, Precision}
import com.paulgoldbaum.influxdbclient.{InfluxDB, Point, WriteException}

import scala.concurrent.ExecutionContext

object InfluxClient {

  case class InfluxDBPoint(measurement: String, timestamp: Long)

  case class InfluxConfig(url: String = "localhost", port: Int = 8086, username: String = "", password: String = "", database: String)

  def apply(config: InfluxConfig)(implicit executor: ExecutionContext): InfluxClient = {
    new InfluxClient(InfluxDB.connect(config.url, config.port), config.database)
  }

}

class InfluxClient(client: InfluxDB, database: String) {

  def write(point: Point)(implicit executor: ExecutionContext): Unit = {
    client.selectDatabase(database).write(point,
      Precision.MILLISECONDS,
      Consistency.ALL,
      "custom_rp").recover { case e: WriteException =>
      println(s"There was an error inserting the data. ${e.getMessage}")
    }
  }

  def write(points: List[Point])(implicit executor: ExecutionContext): Unit = {
    client.selectDatabase(database).bulkWrite(points,
      Precision.MILLISECONDS).recover { case e: WriteException =>
      println(s"There was an error inserting the data. ${e.getMessage}")
    }
  }

  def closeClient(): Unit = client.close()

  def getDatabase(): String = database


}
