package com.endava.flink.twitter.sink.influx

case class InfluxDBPoint(measurement: String, timestamp: Long)