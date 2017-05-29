package com.wix.mysql.store

import java.io.File

import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.config.ArtifactStoreConfig.anArtifactStoreConfig
import com.wix.mysql.distribution.Version
import com.wix.mysql.support.IntegrationTest

class ArtifactStoreTest extends IntegrationTest {

  "EmbeddedMysql artifact store" should {

    "store runtime files in target/ by default" in {
      val mysqld = start(anEmbeddedMysql(Version.v5_7_latest))

      mysqld must haveSystemVariable("basedir", contain(pathFor("/target/", "/mysql-5.7-")))
    }

    "store runtime files in custom location" in {
      val tempDir = System.getProperty("java.io.tmpdir")
      val artifactConfig = anArtifactStoreConfig().withTempDir(tempDir).build()
      val mysqld = start(anEmbeddedMysql(Version.v5_7_latest, artifactConfig))

      mysqld must haveSystemVariable("basedir", contain(pathFor(tempDir, "/mysql-5.7-")))
    }
  }

  def pathFor(basedir: String, subdir: String): String = {
    new File(basedir, subdir).getPath.dropRight(1)
  }
}
