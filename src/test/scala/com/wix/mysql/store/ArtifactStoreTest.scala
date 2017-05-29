package com.wix.mysql.store

import java.io.File
import java.nio.file.Files

import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.config.ArtifactStoreConfig.anArtifactStoreConfig
import com.wix.mysql.distribution.Version
import com.wix.mysql.support.IntegrationTest
import org.apache.commons.io.FileUtils.deleteDirectory
import org.specs2.matcher.FileMatchers

class ArtifactStoreTest extends IntegrationTest with FileMatchers {

  "EmbeddedMysql artifact store" should {

    "store runtime files in './target' by default" in {
      val mysqld = start(anEmbeddedMysql(Version.v5_7_latest))

      mysqld must haveSystemVariable("basedir", contain(pathFor("/target/", "/mysql-5.7-")))
    }

    "store runtime files in custom location" in {
      val tempDir = System.getProperty("java.io.tmpdir")
      val artifactConfig = anArtifactStoreConfig().withTempDir(tempDir).build()
      val mysqld = start(anEmbeddedMysql(Version.v5_7_latest, artifactConfig))

      mysqld must haveSystemVariable("basedir", contain(pathFor(tempDir, "/mysql-5.7-")))
    }

    "store download cache in custom location" in {
      withTempDir { tempDir =>
        val defaultCachePath = anArtifactStoreConfig().build().getDownloadCacheDir
        val artifactConfig = anArtifactStoreConfig().withDownloadCacheDir(tempDir).build()
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
