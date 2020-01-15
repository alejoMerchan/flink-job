package com.endava.flink.twitter.watermark

import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.watermark.Watermark

class TimestampExtractor extends AssignerWithPeriodicWatermarks[String] with Serializable {

  override def extractTimestamp(e: String, prevElementTimestamp: Long) = {
    e.split(",")(1).toLong
  }

  override def getCurrentWatermark(): Watermark = {
    new Watermark(System.currentTimeMillis)
  }

}
