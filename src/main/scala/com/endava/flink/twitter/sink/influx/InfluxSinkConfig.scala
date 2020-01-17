package com.endava.flink.twitter.sink.influx

final case class InfluxSinkConfig(url: String, username: String, password: String, database: String)