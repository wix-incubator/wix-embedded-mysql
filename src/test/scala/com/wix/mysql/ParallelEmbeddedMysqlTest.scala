package com.wix.mysql

import java.util.UUID

import com.wix.mysql.EmbeddedMysql._
import com.wix.mysql.config.MysqldConfig.aMysqldConfig
import com.wix.mysql.distribution.Version
import com.wix.mysql.support.IntegrationTest
import de.flapdoodle.embed.process.io.directories.UserHome
import org.apache.commons.io.FileUtils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class ParallelEmbeddedMysqlTest extends IntegrationTest {

  "EmbeddedMysql instance" should {

    "run 2 instances in parallel" in {
      withCleanRepo {
        val promisedMysql1 = runMysql(onPort = 3310)
        val promisedMysql2 = runMysql(onPort = 3311)

        Await.result(promisedMysql1, 15 minutes)
        Await.result(promisedMysql2, 15 minutes)

        1 mustEqual 1
      }
    }
  }

  def runMysql(onPort: Int) = Future {
      val config1 = aMysqldConfig(Version.v5_7_latest).withPort(onPort).build
      val mysqld1 = withStop(anEmbeddedMysql(config1).start)
    }

  def withCleanRepo[T](f: => T): T = {
    val repository = new UserHome(".embedmysql").asFile()
    val backupFolder = new UserHome(s".embedmysql${UUID.randomUUID().toString}").asFile()
    FileUtils.moveDirectory(repository, backupFolder)
    try {
      f
    } finally {
      println("delete directory")
      FileUtils.deleteDirectory(repository)
      FileUtils.moveDirectory(backupFolder, repository)
    }
  }
}
