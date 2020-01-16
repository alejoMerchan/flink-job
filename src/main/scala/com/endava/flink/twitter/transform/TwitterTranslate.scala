package com.endava.flink.twitter.transform

import java.util

import com.endava.flink.twitter.model.TwitterEvent
import org.apache.flink.api.common.functions.MapFunction
import twitter4j.{HashtagEntity, TwitterObjectFactory}

import scala.collection.JavaConversions._

class TwitterTranslate extends MapFunction[String, TwitterEvent] {

  override def map(t: String): TwitterEvent = {

    val status = TwitterObjectFactory.createStatus(t)

    TwitterEvent(status.getUser.getName,
      status.getCreatedAt,
      System.currentTimeMillis(),
      getHashTags(status.getHashtagEntities)
    )
  }


  private def getHashTags(hashTagsEntities: Array[HashtagEntity]): util.List[java.lang.String] =
    hashTagsEntities.map(hashTag => hashTag.getText).toList


}
