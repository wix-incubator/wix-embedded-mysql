package com.wix.mysql

import com.wix.mysql.EmbeddedMysql._
import com.wix.mysql.config.Charset.{LATIN1, UTF8MB4}
import com.wix.mysql.config.MysqldConfig.aMysqldConfig
import com.wix.mysql.config.SchemaConfig.aSchemaConfig
import com.wix.mysql.distribution.Version.v5_6_latest
import com.wix.mysql.support.IntegrationTest

/**
 * @author viliusl
 * @since 03/07/15
 */
class EmbeddedMysqlTest extends IntegrationTest {

  "EmbeddedMysql instance" should {

    "start with default values" in {
      val config = aMysqldConfig(v5_6_latest).build

      mysqld = anEmbeddedMysql(config).start

      mysqld must
        haveCharsetOf(UTF8MB4) and
        beAvailableOn(3310, "auser", "sa")
    }

    "use custom values provided via MysqldConfig" in {
      val config = aMysqldConfig(v5_6_latest)
        .withCharset(LATIN1)
        .withUser("zeUser", "zePassword")
        .withPort(1112)
        .build

      mysqld = anEmbeddedMysql(config).start

      mysqld must
        haveCharsetOf(LATIN1) and
        beAvailableOn(1112, "zeUser", "zePassword")
    }
  }

  "EmbeddedMysql schemas" should {
    "use defaults" in {
      val config = aMysqldConfig(v5_6_latest).build

      mysqld = anEmbeddedMysql(config)
        .addSchema("aSchema")
        .start

      mysqld must
        haveSchemaCharsetOf(UTF8MB4, "aSchema") and
        beAvailableOn(3310, "auser", "sa", andSchema = "aSchema")
    }

    "use custom values" in {
      val config = aMysqldConfig(v5_6_latest).build
      val schema = aSchemaConfig("aSchema")
        .withCharset(LATIN1)
        .build

      mysqld = anEmbeddedMysql(config)
        .addSchema(schema)
        .start

      mysqld must
        haveSchemaCharsetOf(LATIN1, "aSchema") and
        beAvailableOn(3310, "auser", "sa", andSchema = "aSchema")
    }
  }
}
