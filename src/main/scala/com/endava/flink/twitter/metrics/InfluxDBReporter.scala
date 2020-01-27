package com.endava.flink.twitter.metrics

import com.endava.flink.twitter.client.InfluxClient.InfluxConfig
import com.endava.flink.twitter.client.{Client, InfluxClient}
import com.paulgoldbaum.influxdbclient.Point
import org.apache.flink.metrics._
import org.apache.flink.metrics.reporter.{AbstractReporter, Scheduled}

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

case class InfluxMetric(name: String, value: String, filter: String)

class InfluxDBReporter extends AbstractReporter with Scheduled with Client {

  type Filter = String
  var influxClient: InfluxClient = _
  var filters: List[Filter] = _

  override def close(): Unit = {
    influxClient.closeClient()
  }

  override def open(metricConfig: MetricConfig): Unit = {
    influxClient = InfluxClient(InfluxConfig(database = metricConfig.getString("database", "metrics")))
    filters = getFilters(metricConfig.getString("filter", ""))
    println("--- filters: " + filters)
  }

  private def getFilters(stringOfFilters: String): List[Filter] = {
    stringOfFilters.isEmpty match {
      case true => List.empty[String]
      case false => stringOfFilters.split(",").toList
    }
  }

  override def filterCharacters(s: String): String = s

  override def report(): Unit = {
    Try {
      val metrics = filterMetrics(this.counters.asScala)
      influxClient.write(getInfluxPoints(metrics, influxClient.getDatabase()))
    } match {
      case Success(_) =>
        println("--- success push metric")
        cleanCounter()
      case Failure(err) =>
        println("--- failing pushing metrics " + err.getMessage)
    }
  }

  private def cleanCounter():Unit = {
    counters.asScala.toList.foreach {
      counter =>
        filters.foreach {
          filter =>
            if (counter._2.contains(filter)) {
              val a = counter._1.getCount
              counter._1.dec(a)
            }
        }
    }
  }

  private def getInfluxPoints(influxMetrics: List[InfluxMetric], database: String): List[Point] = {
    influxMetrics.map {
      influxMetric =>
        println("publishing: " + influxMetric.filter + " --- "+influxMetric.value)
        Point(database).addField(influxMetric.filter, influxMetric.value)
    }
  }

  private def filterMetrics[T <: Metric](metrics: scala.collection.mutable.Map[T, String]): List[InfluxMetric] = {
    metrics.flatMap {
      metric =>
        filters.foldLeft(List.empty[InfluxMetric]) {
          (isInList, filter) =>
            if (metric._2.contains(filter)) {
              InfluxMetric(metric._2, metricValue(metric._1), filter) :: isInList
            } else {
              isInList
            }
        }

    }.toList

  }

  private def metricValue(m: Metric): String = {
    m match {
      case g: Gauge[_] => g.getValue.toString
      case c: Counter => c.getCount.toString
      case h: Histogram => h.getStatistics.toString
    }
  }

}
