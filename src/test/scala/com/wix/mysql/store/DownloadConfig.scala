package com.wix.mysql.store

import java.io.File
import java.nio.file.Files

import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.config.DownloadConfig.aDownloadConfig
import com.wix.mysql.distribution.Version
import com.wix.mysql.support.IntegrationTest
import org.apache.commons.io.FileUtils.deleteDirectory
import org.specs2.matcher.FileMatchers

class DownloadConfig extends IntegrationTest with FileMatchers {

  "EmbeddedMysql download config" should {

    "store download cache in custom location" in {
      withTempDir { tempDir =>
        val defaultCachePath = aDownloadConfig().build().getDownloadCacheDir
        val artifactConfig = aDownloadConfig().withDownloadCacheDir(tempDir).build()
        val mysqld = start(anEmbeddedMysql(Version.v5_7_latest, artifactConfig))

        tempDir must not(beEqualToIgnoringSep(defaultCachePath))
        new File(pathFor(tempDir, "extracted")) must beADirectory and exist
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
