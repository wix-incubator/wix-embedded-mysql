package com.wix.mysql.config

import java.io.File
import java.nio.file.Files

import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.config.DownloadConfig.aDownloadConfig
import com.wix.mysql.distribution.Version
import com.wix.mysql.support.{MysqlCacheServingHttpServer, IntegrationTest}
import org.apache.commons.io.FileUtils.deleteDirectory
import org.nanohttpd.router.RouterNanoHTTPD
import org.specs2.matcher.FileMatchers
import org.specs2.mutable.BeforeAfter
import org.nanohttpd.router.RouterNanoHTTPD.StaticPageHandler
import java.io.BufferedInputStream
import java.io.IOException

class DownloadConfigTest extends IntegrationTest with FileMatchers {

  "EmbeddedMysql download config" should {

    "store download cache in custom location" in {
      withTempDir { tempDir =>
        val defaultCachePath = aDownloadConfig().build().getDownloadCacheDir
        val downloadConfig = aDownloadConfig().withDownloadCacheDir(tempDir).build()
        val mysqld = start(anEmbeddedMysql(Version.v5_7_latest, downloadConfig))

        tempDir must not(beEqualToIgnoringSep(defaultCachePath))
        new File(pathFor(tempDir, "extracted")) must beADirectory and exist
      }
    }

    "support custom download base url" in new context {

      withTempDir { tempDir =>
        val downloadConfig = aDownloadConfig()
          .withDownloadCacheDir(tempDir)
          .withBaseUrl(s"http://localhost:${httpServer.port}")
          .build()

        start(anEmbeddedMysql(Version.v5_7_latest, downloadConfig))

        new File(pathFor(tempDir, "extracted")) must beADirectory and exist
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

  def pathFor(basedir: String, subdir: String): String = {
    new File(basedir, subdir).getPath
  }

  def withTempDir[T](f: String => T): T = {
    val tempDir = Files.createTempDirectory("embed-mysql-test").toFile

    try {
      f(tempDir.getAbsolutePath)
    } finally {
      deleteDirectory(tempDir)
    }
  }

}
