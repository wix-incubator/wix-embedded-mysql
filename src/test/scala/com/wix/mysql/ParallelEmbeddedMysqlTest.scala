package com.wix.mysql

import com.wix.mysql.EmbeddedMysql._
import com.wix.mysql.config.MysqldConfig.{SystemDefaults, aMysqldConfig}
import com.wix.mysql.distribution.Version
import com.wix.mysql.support.IntegrationTest

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
    val config = aMysqldConfig(Version.v5_7_latest).withPort(onPort).build
    val mysqld = start(anEmbeddedMysql(config))

    mysqld must beAvailableOn(onPort, "auser", "sa", SystemDefaults.SCHEMA)
  }
}
