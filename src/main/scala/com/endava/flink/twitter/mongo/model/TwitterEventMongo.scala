package com.endava.flink.twitter.mongo.model

import java.util
import java.util.Date

import org.bson.types.ObjectId

case class TwitterEventMongo(_id: ObjectId, author: String, transactionTime: Long, date: Date, hashTags: util.List[java.lang.String]) extends MongoModel

object TwitterEventMongo {
  def apply(author: String, transactionTime: Long, date: Date, hashTags: util.List[java.lang.String]): TwitterEventMongo =
    new TwitterEventMongo(new ObjectId(), author, transactionTime, date, hashTags)
}
