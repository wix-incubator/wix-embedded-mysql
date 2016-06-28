package com.wix.mysql

import java.io.File
import java.net.Proxy.Type
import java.net.{InetAddress, SocketAddress}

import com.wix.mysql.EmbeddedMysql._
import com.wix.mysql.config.Charset.{LATIN1, UTF8MB4}
import com.wix.mysql.config.DownloadConfig
import com.wix.mysql.config.DownloadConfig.aDownloadConfig
import com.wix.mysql.config.MysqldConfig.{SystemDefaults, aMysqldConfig}
import com.wix.mysql.config.SchemaConfig.aSchemaConfig
import com.wix.mysql.distribution.Version
import com.wix.mysql.distribution.Version.v5_6_latest
import com.wix.mysql.exceptions.CommandFailedException
import com.wix.mysql.support.IntegrationTest
import de.flapdoodle.embed.process.io.directories.UserHome
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FileUtils.deleteDirectory
import org.littleshoot.proxy.HttpProxyServer
import org.littleshoot.proxy.impl.DefaultHttpProxyServer

class EmbeddedMysqlTest extends IntegrationTest {

  "EmbeddedMysql instance" should {

    "start with default values" in {
      val config = aMysqldConfig(Version.v5_7_latest).build

      mysqld = anEmbeddedMysql(config).start

      mysqld must
        haveCharsetOf(UTF8MB4) and
        beAvailableOn(3310, "auser", "sa", SystemDefaults.SCHEMA) and
        haveServerTimezoneMatching("UTC")

    }

    "use custom values provided via MysqldConfig" in {
      val config = aMysqldConfig(v5_6_latest)
        .withCharset(LATIN1)
        .withUser("zeUser", "zePassword")
        .withPort(1112)
        .withTimeZone("US/Michigan")
        .build

      mysqld = anEmbeddedMysql(config).start

      mysqld must
        haveCharsetOf(LATIN1) and
        beAvailableOn(1112, "zeUser", "zePassword", SystemDefaults.SCHEMA) and
        haveServerTimezoneMatching("US/Michigan")
    }

    def cleanDownloadedFiles() = deleteDirectory(new UserHome(".embedmysql").asFile())

    def withProxyOn[T](port: Int)(f: Int => T): T = {
      val proxyBootstrap = DefaultHttpProxyServer.bootstrap().withPort(port)
      var proxyServer: Option[HttpProxyServer] = None

      try {
        proxyServer = Some(proxyBootstrap.start())
        f(port)
      } finally {
        proxyServer.map(_.stop())
      }
    }

    "allow to provide network proxy" in {
      withProxyOn(3210) { port =>
        cleanDownloadedFiles()

        val config = aMysqldConfig(Version.v5_7_latest).build

        mysqld = anEmbeddedMysql(config)
          .withDownloadConfig(aDownloadConfig().withProxy("127.0.0.1", port).build())
          .addSchema("aschema")
          .start

        mysqld must beAvailableOn(config, "aschema")

      }
    }
  }

  "EmbeddedMysql schema reload" should {
    "reset schema" in {
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

  "EmbeddedMysql schema creation" should {
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

    "inherit schema charset from instance" in {
      val config = aMysqldConfig(v5_6_latest).withCharset(LATIN1).build
      val schema = aSchemaConfig("aSchema").build

      mysqld = anEmbeddedMysql(config)
        .addSchema(schema)
        .start

      mysqld must
        haveSchemaCharsetOf(LATIN1, "aSchema") and
        beAvailableOn(3310, "auser", "sa", andSchema = "aSchema")
    }

    "apply migrations when providing single file" in {
      mysqld = anEmbeddedMysql(v5_6_latest)
        .addSchema("aSchema", aMigrationWith("create table t1 (col1 INTEGER);"))
        .start

      aQuery(onSchema = "aSchema", sql = "select count(col1) from t1;") must beSuccessful
    }

    "apply migrations from multiple files" in {
      mysqld = anEmbeddedMysql(v5_6_latest)
        .addSchema("aSchema",
          aMigrationWith("create table t1 (col1 INTEGER);"),
          aMigrationWith("create table t2 (col1 INTEGER);"))
        .start

      aQuery(onSchema = "aSchema", sql = "select count(col1) from t1;") must beSuccessful
      aQuery(onSchema = "aSchema", sql = "select count(col1) from t2;") must beSuccessful
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

      mysqld = anEmbeddedMysql(v5_6_latest)
        .addSchema(config)
        .start

      aQuery(onSchema = "aSchema", sql = "select count(col1) from t1;") must beSuccessful
      aQuery(onSchema = "aSchema", sql = "select count(col1) from t2;") must beSuccessful
      aQuery(onSchema = "aSchema", sql = "select count(col1) from t3;") must beSuccessful
      aQuery(onSchema = "aSchema", sql = "select count(col2) from t4;") must beSuccessful
    }
  }

  "EmbeddedMysql schema modification" should {

    "drop existing schema" in {
      val schemaConfig = aSchemaConfig("aSchema").build

      mysqld = anEmbeddedMysql(aMysqldConfig(v5_6_latest).build)
        .addSchema(schemaConfig)
        .start

      mysqld must haveSchemaCharsetOf(UTF8MB4, schemaConfig.getName)

      mysqld.dropSchema(schemaConfig)

      mysqld must notHaveSchema(schemaConfig.getName)
    }

    "fail on dropping of non existing schema" in {
      val schemaConfig = aSchemaConfig("aSchema").build

      mysqld = anEmbeddedMysql(aMysqldConfig(v5_6_latest).build)
        .start

      mysqld must notHaveSchema(schemaConfig.getName)

      mysqld.dropSchema(schemaConfig) must throwA[CommandFailedException]
    }

    "add schema after mysqld start" in {
      val schemaConfig = aSchemaConfig("aSchema").build

      mysqld = anEmbeddedMysql(aMysqldConfig(v5_6_latest).build)
        .start

      mysqld must notHaveSchema(schemaConfig.getName)

      mysqld.addSchema(schemaConfig)

      mysqld must haveSchemaCharsetOf(UTF8MB4, schemaConfig.getName)
    }

    "fail on adding existing schema" in {
      val schemaConfig = aSchemaConfig("aSchema").build

      mysqld = anEmbeddedMysql(aMysqldConfig(v5_6_latest).build)
        .addSchema(schemaConfig)
        .start

      mysqld must haveSchemaCharsetOf(UTF8MB4, schemaConfig.getName)

      mysqld.addSchema(schemaConfig) must throwA[CommandFailedException]
    }
  }
}