package com.endava.flink.twitter.sink

import com.endava.flink.twitter.model.TwitterEvent
import com.endava.flink.twitter.mongo.model.{MongoModel, TwitterEventMongo}
import com.endava.flink.twitter.sink.MongoSink.MongoSinkConfig
import org.apache.flink.configuration.Configuration
import org.apache.flink.metrics.Counter
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}
import org.mongodb.scala.{Completed, MongoClient, MongoCollection, Observable, Observer}


object MongoSink {

  case class MongoSinkConfig(uri: String, db: String, collection: String)

  def apply(config: MongoSinkConfig): MongoSink = {
    new MongoSink(config)
  }

}

class MongoSink(config: MongoSinkConfig) extends RichSinkFunction[TwitterEvent] {

  var mongoClient: MongoClient = _

  @transient private var outSuccessCounter: Counter = _
  @transient private var outErrorCounter: Counter = _

  override def open(parameters: Configuration): Unit = {
    mongoClient = MongoClient()

    this.outErrorCounter = getRuntimeContext.getMetricGroup.addGroup("twitter-metrics").counter("outErrorCounter")
    this.outSuccessCounter = getRuntimeContext.getMetricGroup.addGroup("twitter-metrics").counter("outSuccessCounter")
  }

  override def invoke(event: TwitterEvent, context: SinkFunction.Context[_]): Unit = {


    val db = mongoClient.getDatabase(config.db).withCodecRegistry(MongoModel.codecRegistry)
    val collection: MongoCollection[TwitterEventMongo] = db.getCollection(config.collection)

    val insertObservable: Observable[Completed] = collection.insertOne(toMongoModel(event))

    insertObservable.subscribe {
      new Observer[Completed] {

        override def onError(e: Throwable): Unit = {
          println(s"onError: $e")
          outErrorCounter.inc()
        }


        override def onComplete(): Unit = {
          outSuccessCounter.inc()
          println("onComplete")
        }

        override def onNext(result: Completed): Unit = println(s"onNext: $result")
      }
    }

  }

  override def close(): Unit = {
    mongoClient.close()
  }

  private def toMongoModel(event: TwitterEvent): TwitterEventMongo = {
    TwitterEventMongo(event.author, event.transactionTime, event.date, event.hashTags)
  }

}
