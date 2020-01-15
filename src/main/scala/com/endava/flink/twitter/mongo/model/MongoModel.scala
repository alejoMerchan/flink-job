package com.endava.flink.twitter.mongo.model

import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.types.ObjectId
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._

trait MongoModel {
  def _id: ObjectId
}

object MongoModel {

  val codecRegistry = fromRegistries(fromProviders(classOf[TwitterEventMongo]), DEFAULT_CODEC_REGISTRY)

}
