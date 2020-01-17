package com.endava.flink.twitter.sink.influx

final case class InfluxDBPoint(measurement: String, timestamp: Long)