package com.wix.mysql

import java.io.File

import com.wix.mysql.EmbeddedMysql._
import com.wix.mysql.config.Charset.{LATIN1, UTF8MB4}
import com.wix.mysql.config.MysqldConfig.{SystemDefaults, aMysqldConfig}
import com.wix.mysql.config.SchemaConfig.aSchemaConfig
import com.wix.mysql.distribution.Version.v5_6_latest
import com.wix.mysql.support.IntegrationTest
import org.springframework.dao.DataAccessException


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
        beAvailableOn(3310, "auser", "sa", SystemDefaults.SCHEMA)
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
        beAvailableOn(1112, "zeUser", "zePassword", SystemDefaults.SCHEMA)
    }

    "reset schema with schema name and migration files" in {
      mysqld = anEmbeddedMysql(v5_6_latest)
        .addSchema("aSchema", aMigrationWith("create table t1 (col1 INTEGER);"))
        .start

      aQuery(onSchema = "aSchema", sql = "select count(col1) from t1;") must beSuccessful

      mysqld.reloadSchema("aSchema", aMigrationWith("create table t2 (col1 INTEGER);"))

      aQuery(onSchema = "aSchema", sql = "select count(col1) from t2;") must beSuccessful
      aQuery(onSchema = "aSchema", sql = "select count(col1) from t1;") must failWith("Table 'aschema.t1' doesn't exist")
    }

    "reset schema with schema config" in {
      val config = aSchemaConfig("aSchema")
        .withScripts(aMigrationWith("create table t1 (col1 INTEGER);"))
        .build

      mysqld = anEmbeddedMysql(v5_6_latest)
        .addSchema(config)
        .start

      anUpdate(onSchema = "aSchema", sql = "insert into t1 values(10)") must beSuccessful
      aSelect[java.lang.Long](onSchema = "aSchema", sql = "select col1 from t1 where col1 = 10;") mustEqual 10

      mysqld.reloadSchema(config)

      aSelect[java.lang.Long](onSchema = "aSchema", sql = "select col1 from t1 where col1 = 10;") must
        failWith("Incorrect result size: expected 1, actual 0")
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
