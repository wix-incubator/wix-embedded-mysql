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
      withTempDir { tempDir =>
        val defaultCachePath = aDownloadConfig().build().getCacheDir
        val downloadConfig = aDownloadConfig().withCacheDir(tempDir).build()
        val mysqld = start(anEmbeddedMysql(testConfigBuilder.build, downloadConfig))

        tempDir must not(beEqualToIgnoringSep(defaultCachePath))
        aPath(tempDir, "extracted") must beADirectory and exist
      }
    }

    "uses custom download base url" in {
      withTempDir { tempDir =>
        val downloadConfig = aDownloadConfig()
          .withCacheDir(tempDir)
          .withBaseUrl(s"http://localhost:2222")
          .build()

        start(anEmbeddedMysql(testConfigBuilder.build, downloadConfig)) must throwA[DistributionException].like {
          case e => e.getMessage must contain("Could not open inputStream for http://localhost:2222/MySQL-5.7")
        }
      }
    }

    "downloads via custom download base url" in new context {
      val mysqldConfig = testConfigBuilder.build

      ensureVersionPresentInCache(mysqldConfig)

      withTempDir { tempDir =>
        val downloadConfig = aDownloadConfig()
          .withDownloadCacheDir(tempDir)
          .withBaseUrl(s"http://localhost:${httpServer.port}")
          .build()

        start(anEmbeddedMysql(mysqldConfig, downloadConfig))

        aPath(tempDir, "extracted") must beADirectory and exist
      }
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
