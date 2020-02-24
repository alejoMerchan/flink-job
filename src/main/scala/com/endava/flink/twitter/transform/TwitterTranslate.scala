package com.endava.flink.twitter.transform

import java.util

import com.endava.flink.twitter.model.TwitterEvent
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.configuration.Configuration
import org.apache.flink.metrics.Counter
import twitter4j.{HashtagEntity, TwitterObjectFactory}

import scala.collection.JavaConversions._

class TwitterTranslate extends RichMapFunction[String, TwitterEvent] {

  @transient private var counter: Counter = _


  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    this.counter = getRuntimeContext.getMetricGroup.addGroup("twitter-metrics").counter("incomingCount")

  }

  override def map(t: String): TwitterEvent = {
    this.counter.inc()
    val status = TwitterObjectFactory.createStatus(t)
//    print("Record Transformed.." + this.counter.getCount)
    TwitterEvent(status.getUser.getName,
      status.getCreatedAt,
      System.currentTimeMillis(),
      getHashTags(status.getHashtagEntities)
    )
  }


  private def getHashTags(hashTagsEntities: Array[HashtagEntity]): util.List[java.lang.String] =
    hashTagsEntities.map(hashTag => hashTag.getText).toList


}
