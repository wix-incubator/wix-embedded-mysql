package com.wix.mysql

import java.util.concurrent.TimeUnit

import com.wix.mysql.EmbeddedMysql._
import com.wix.mysql.config.MysqldConfig.SystemDefaults
import com.wix.mysql.support.IntegrationTest
import com.wix.mysql.support.IntegrationTest.testConfigBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class ParallelEmbeddedMysqlTest extends IntegrationTest {

  "EmbeddedMysql instance" should {

    "run 2 instances in parallel" in {
      withCleanRepo {
        Seq(runMysql(onPort = 3310), runMysql(onPort = 3311)).map(Await.result(_, 15 minutes)) must beSuccessful
      }
    }
  }

  def runMysql(onPort: Int) = Future {
    val config = testConfigBuilder.withPort(onPort).withTimeout(2, TimeUnit.MINUTES).build
    val mysqld = start(anEmbeddedMysql(config))

    mysqld must beAvailableOn(onPort, "auser", "sa", SystemDefaults.SCHEMA)
  }
}
