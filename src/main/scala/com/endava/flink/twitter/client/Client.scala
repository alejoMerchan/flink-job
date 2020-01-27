package com.endava.flink.twitter.client

import java.util.concurrent.{ExecutorService, Executors}

import scala.concurrent.ExecutionContext

trait Client {

  implicit val ec: ExecutionContext = new ExecutionContext {
    val executors = 20
    val threadPool: ExecutorService = Executors.newFixedThreadPool(executors)

    def execute(runnable: Runnable): Unit = {
      threadPool.submit(runnable)
    }

    def reportFailure(t: Throwable): Unit = {
      println(s"Problem in the ThreadPool configuration: ${t.getMessage}")
    }
  }

}
