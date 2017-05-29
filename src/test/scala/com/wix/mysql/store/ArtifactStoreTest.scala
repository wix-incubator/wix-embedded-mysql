package com.wix.mysql.store

import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.distribution.Version
import com.wix.mysql.support.IntegrationTest

class ArtifactStoreTest extends IntegrationTest {

  "EmbeddedMysql instance" should {

    "store extracted files in target/ by default" in {
      val mysqld = start(anEmbeddedMysql(Version.v5_7_latest))

      mysqld must haveSystemVariable("basedir", contain("/target/mysql-5.7-"))
    }
  }

}
