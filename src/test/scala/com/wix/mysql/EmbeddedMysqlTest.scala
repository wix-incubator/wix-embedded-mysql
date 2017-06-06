package com.wix.mysql

import java.io.File
import java.util.concurrent.TimeUnit

import com.wix.mysql.EmbeddedMysql._
import com.wix.mysql.config.Charset._
import com.wix.mysql.config.MysqldConfig.{SystemDefaults, aMysqldConfig}
import com.wix.mysql.config.SchemaConfig.aSchemaConfig
import com.wix.mysql.exceptions.CommandFailedException
import com.wix.mysql.support.IntegrationTest
import com.wix.mysql.support.IntegrationTest.testVersion

class EmbeddedMysqlTest extends IntegrationTest {

  "EmbeddedMysql instance" should {

    "start with default values" in {
      val config = aMysqldConfig(testVersion).build

      val mysqld = start(anEmbeddedMysql(config))

      mysqld must
        haveCharsetOf(UTF8MB4) and
        beAvailableOn(3310, "auser", "sa", SystemDefaults.SCHEMA) and
        haveServerTimezoneMatching("UTC") and
        haveSystemVariable("basedir", contain(pathFor("/target/", "/mysql-5.7-")))
    }

    "use custom values provided via MysqldConfig" in {
      val tempDir = System.getProperty("java.io.tmpdir")
      val config = aMysqldConfig(testVersion)
        .withCharset(LATIN1)
        .withUser("zeUser", "zePassword")
        .withPort(1112)
        .withTimeZone("US/Michigan")
        .withTempDir(tempDir)
        .build

      val mysqld = start(anEmbeddedMysql(config))

      mysqld must
        haveCharsetOf(LATIN1) and
        beAvailableOn(1112, "zeUser", "zePassword", SystemDefaults.SCHEMA) and
        haveServerTimezoneMatching("US/Michigan") and
        haveSystemVariable("basedir", contain(pathFor(tempDir, "/mysql-5.7-")))
    }

    "accept system variables" in {
      val config = aMysqldConfig(testVersion)
        .withServerVariable("max_connect_errors", 666)
        .build

      val mysqld = start(anEmbeddedMysql(config))

      mysqld must haveSystemVariable("max_connect_errors", ===("666"))
    }

    "not allow to override library-managed system variables" in {
      val config = aMysqldConfig(testVersion)
        .withTimeZone("US/Michigan")
        .withServerVariable("default-time-zone", "US/Eastern")
        .build

      start(anEmbeddedMysql(config)) must throwA[RuntimeException]
    }

    "respect provided timeout" in {
      start(anEmbeddedMysql(aMysqldConfig(testVersion).withTimeout(10, TimeUnit.MILLISECONDS).build)) must
        throwA[RuntimeException].like { case e => e.getMessage must contain("0 sec") }
    }
  }

  "EmbeddedMysql schema reload" should {
    "reset schema" in {
      val mysqldConfig = aMysqldConfig(testVersion)
        .withTimeout(60, TimeUnit.SECONDS)
        .build

      val schemaConfig = aSchemaConfig("aSchema")
        .withScripts(aMigrationWith("create table t1 (col1 INTEGER);"))
        .build

      val mysqld = start(anEmbeddedMysql(mysqldConfig).addSchema(schemaConfig))

      anUpdate(mysqld, onSchema = "aSchema", sql = "insert into t1 values(10)") must beSuccessful
      aSelect[java.lang.Long](mysqld, onSchema = "aSchema", sql = "select col1 from t1 where col1 = 10;") mustEqual 10

      mysqld.reloadSchema(schemaConfig)

      aSelect[java.lang.Long](mysqld, onSchema = "aSchema", sql = "select col1 from t1 where col1 = 10;") must
        failWith("Incorrect result size: expected 1, actual 0")
    }
  }

  "EmbeddedMysql schema creation" should {
    "use defaults" in {
      val config = aMysqldConfig(testVersion).build

      val mysqld = start(anEmbeddedMysql(config).addSchema("aSchema"))

      mysqld must
        haveSchemaCharsetOf(UTF8MB4, "aSchema") and
        beAvailableOn(3310, "auser", "sa", andSchema = "aSchema")
    }

    "use custom values" in {
      val config = aMysqldConfig(testVersion).build
      val schema = aSchemaConfig("aSchema")
        .withCharset(LATIN1)
        .build

      val mysqld = start(anEmbeddedMysql(config).addSchema(schema))

      mysqld must
        haveSchemaCharsetOf(LATIN1, "aSchema") and
        beAvailableOn(3310, "auser", "sa", andSchema = "aSchema")
    }

    "inherit schema charset from instance" in {
      val config = aMysqldConfig(testVersion).withCharset(LATIN1).build
      val schema = aSchemaConfig("aSchema").build

      val mysqld = start(anEmbeddedMysql(config).addSchema(schema))

      mysqld must
        haveSchemaCharsetOf(LATIN1, "aSchema") and
        beAvailableOn(3310, "auser", "sa", andSchema = "aSchema")
    }

    "apply migrations when providing single file" in {
      val mysqld = start(anEmbeddedMysql(testVersion)
        .addSchema("aSchema", aMigrationWith("create table t1 (col1 INTEGER);")))

      aQuery(mysqld, onSchema = "aSchema", sql = "select count(col1) from t1;") must beSuccessful
    }

    "apply migrations from multiple files" in {
      val mysqld = start(anEmbeddedMysql(testVersion)
        .addSchema("aSchema",
          aMigrationWith("create table t1 (col1 INTEGER);"),
          aMigrationWith("create table t2 (col1 INTEGER);"))
      )

      aQuery(mysqld, onSchema = "aSchema", sql = "select count(col1) from t1;") must beSuccessful
      aQuery(mysqld, onSchema = "aSchema", sql = "select count(col1) from t2;") must beSuccessful
    }

    "apply migrations via SchemaConfig" in {
      val config = aSchemaConfig("aSchema")
        .withScripts(
          aMigrationWith("create table t1 (col1 INTEGER);"),
          aMigrationWith("create table t2 (col1 INTEGER);"))
        .withCommands(
          "create table t3 (col1 INTEGER);\n" +
            "create table t4 (col2 INTEGER)")
        .build

      val mysqld = start(anEmbeddedMysql(testVersion).addSchema(config))

      aQuery(mysqld, onSchema = "aSchema", sql = "select count(col1) from t1;") must beSuccessful
      aQuery(mysqld, onSchema = "aSchema", sql = "select count(col1) from t2;") must beSuccessful
      aQuery(mysqld, onSchema = "aSchema", sql = "select count(col1) from t3;") must beSuccessful
      aQuery(mysqld, onSchema = "aSchema", sql = "select count(col2) from t4;") must beSuccessful
    }
  }

  "EmbeddedMysql schema modification" should {

    "drop existing schema" in {
      val schemaConfig = aSchemaConfig("aSchema").build

      val mysqld = start(anEmbeddedMysql(aMysqldConfig(testVersion).build).addSchema(schemaConfig))

      mysqld must haveSchemaCharsetOf(UTF8MB4, schemaConfig.getName)

      mysqld.dropSchema(schemaConfig)

      mysqld must notHaveSchema(schemaConfig.getName)
    }

    "fail on dropping of non existing schema" in {
      val schemaConfig = aSchemaConfig("aSchema").build

      val mysqld = start(anEmbeddedMysql(aMysqldConfig(testVersion).build))

      mysqld must notHaveSchema(schemaConfig.getName)

      mysqld.dropSchema(schemaConfig) must throwA[CommandFailedException]
    }

    "add schema after mysqld start" in {
      val schemaConfig = aSchemaConfig("aSchema").build

      val mysqld = start(anEmbeddedMysql(aMysqldConfig(testVersion).build))

      mysqld must notHaveSchema(schemaConfig.getName)

      mysqld.addSchema(schemaConfig)

      mysqld must haveSchemaCharsetOf(UTF8MB4, schemaConfig.getName)
    }

    "fail on adding existing schema" in {
      val schemaConfig = aSchemaConfig("aSchema").build

      val mysqld = start(anEmbeddedMysql(aMysqldConfig(testVersion).build).addSchema(schemaConfig))

      mysqld must haveSchemaCharsetOf(UTF8MB4, schemaConfig.getName)

      mysqld.addSchema(schemaConfig) must throwA[CommandFailedException]
    }
  }

  def pathFor(basedir: String, subdir: String): String = {
    new File(basedir, subdir).getPath
  }

}