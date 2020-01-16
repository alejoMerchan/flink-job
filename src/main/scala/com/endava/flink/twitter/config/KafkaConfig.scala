package com.endava.flink.twitter.config

import java.util.Properties

case class KafkaConfig(properties: Properties, topics: List[String])
