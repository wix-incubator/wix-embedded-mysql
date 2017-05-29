package com.wix.mysql.store

import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.config.ArtifactStoreConfig.anArtifactStoreConfig
import com.wix.mysql.distribution.Version
import com.wix.mysql.support.IntegrationTest

class ArtifactStoreTest extends IntegrationTest {

  "EmbeddedMysql instance" should {

    "store runtime files in target/ by default" in {
      val mysqld = start(anEmbeddedMysql(Version.v5_7_latest))

      mysqld must haveSystemVariable("basedir", contain("/target/mysql-5.7-"))
    }

    "store runtime files in custom location" in {
      val tempDir = System.getProperty("java.io.tmpdir")
      val artifactConfig = anArtifactStoreConfig().withTempDir(tempDir).build()
      val mysqld = start(anEmbeddedMysql(Version.v5_7_latest, artifactConfig))

      mysqld must haveSystemVariable("basedir", contain(s"${tempDir}mysql-5.7-"))
    }
  }
}
