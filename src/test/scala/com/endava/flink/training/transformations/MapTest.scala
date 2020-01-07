package com.endava.flink.training.transformations


import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment}
import org.apache.flink.test.util.{MiniClusterResourceConfiguration, MiniClusterWithClientResource}
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import org.apache.flink.streaming.api.scala._ // the streaming api has all the flink data types

class MapTest extends FlatSpec with Matchers with BeforeAndAfter {

  val flinkCluster = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder().
    setNumberSlotsPerTaskManager(1).
    setNumberTaskManagers(1).
    build())

  before {
    flinkCluster.before()
  }

  after {
    flinkCluster.after()
  }

  "MapTest 1" should "use map transformation" in {
    val env = StreamExecutionEnvironment.getExecutionEnvironment


    val events = env.fromElements(1, 2, 3, 4, 5)
    val result = events.map(x => x + 1)
    result.print()

    env.execute("testing map transformation")

  }


}
