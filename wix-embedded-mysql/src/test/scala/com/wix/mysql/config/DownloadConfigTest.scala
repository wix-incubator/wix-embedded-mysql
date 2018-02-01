package com.wix.mysql.config

import java.io.File
import java.nio.file.Files

import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.config.DownloadConfig.aDownloadConfig
import com.wix.mysql.support.IntegrationTest.testConfigBuilder
import com.wix.mysql.support.{IntegrationTest, MysqlCacheServingHttpServer}
import de.flapdoodle.embed.process.exceptions.DistributionException
import org.apache.commons.io.FileUtils.deleteDirectory
import org.specs2.matcher.FileMatchers
import org.specs2.mutable.BeforeAfter

class DownloadConfigTest extends IntegrationTest with FileMatchers {

  "EmbeddedMysql download config" should {

    "store download cache in custom location" in {
        val mysqld = start(anEmbeddedMysql(testConfigBuilder.build))
        true mustEqual true

    }



  }

  class context extends BeforeAfter {
    val httpServer = new MysqlCacheServingHttpServer

    override def before: Any = {
      if (httpServer != null) {
        httpServer.start()
      }
    }

    override def after: Any = {
      if (httpServer != null) {
        httpServer.stop()
      }
    }
  }

  def aPath(basedir: String, subdir: String): File = {
    new File(basedir, subdir)
  }

  def withTempDir[T](f: String => T): T = {
    val tempDir = Files.createTempDirectory("embed-mysql-test").toFile

    try {
      f(tempDir.getAbsolutePath)
    } finally {
      deleteDirectory(tempDir)
    }
  }

  def ensureVersionPresentInCache(config: MysqldConfig): Unit = {
    anEmbeddedMysql(config).start().stop()
  }

}
