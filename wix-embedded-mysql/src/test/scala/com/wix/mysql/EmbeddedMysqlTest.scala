package com.wix.mysql

import com.wix.mysql.EmbeddedMysql._
import com.wix.mysql.config.Charset
import com.wix.mysql.config.Charset.{LATIN1, UTF8MB4}
import com.wix.mysql.config.MysqldConfig.aMysqldConfig
import com.wix.mysql.config.SchemaConfig.aSchemaConfig
import com.wix.mysql.distribution.Version.v5_6_latest
import org.specs2.specification.{AfterEach, Scope}

/**
 * @author viliusl
 * @since 03/07/15
 */
class EmbeddedMysqlTest extends IntegrationTest with AfterEach {
  var mysqld: EmbeddedMysql = _

  trait Context extends Scope {

    def serverCharset = {
      val ds = aDataSource(mysqld.getConfig, "information_schema")
      Charset.aCharset(
        aSelect[String](ds, sql = "SELECT variable_value FROM information_schema.global_variables WHERE variable_name = 'character_set_server';"),
        aSelect[String](ds, sql = "SELECT variable_value FROM information_schema.global_variables WHERE variable_name = 'collation_server';"))
    }

    def schemaCharset(schema: String) = {
      val ds = aDataSource(mysqld.getConfig, schema)
      Charset.aCharset(
        aSelect[String](ds, sql = s"SELECT default_character_set_name FROM information_schema.SCHEMATA where SCHEMA_NAME = '$schema';"),
        aSelect[String](ds, sql = s"SELECT DEFAULT_COLLATION_NAME FROM information_schema.SCHEMATA where SCHEMA_NAME = '$schema';"))
    }

    def userCanConnect(user: String, password: String, port: Int, schema: String = "information_schema") =
      aSelect[java.lang.Long](aDataSource(user, password, port, schema), "select 1;") == 1
  }

  "EmbeddedMysql instance" should {

    "start with default values" in new Context {
      val config = aMysqldConfig(v5_6_latest).build

      mysqld = anEmbeddedMysql(config).start

      serverCharset mustEqual UTF8MB4
      userCanConnect("auser", "sa", 3310) must beTrue
    }

    "use custom values provided via MysqldConfig" in new Context {
      val config = aMysqldConfig(v5_6_latest)
        .withCharset(LATIN1)
        .withUser("zeUser", "zePassword")
        .withPort(1112)
        .build

      mysqld = anEmbeddedMysql(config).start

      serverCharset mustEqual LATIN1
      userCanConnect("zeUser", "zePassword", 1112) must beTrue
    }
  }

  "EmbeddedMysql schemas" should {
    "use defaults" in new Context {
      val config = aMysqldConfig(v5_6_latest).build

      mysqld = anEmbeddedMysql(config)
        .addSchema("aSchema")
        .start

      schemaCharset("aSchema") mustEqual UTF8MB4
      userCanConnect("auser", "sa", 3310, "aSchema") must beTrue
    }

    "use custom values" in new Context {
      val config = aMysqldConfig(v5_6_latest).build
      val schema = aSchemaConfig("aSchema")
        .withCharset(LATIN1)
        .build

      mysqld = anEmbeddedMysql(config)
        .addSchema(schema)
        .start

      schemaCharset("aSchema") mustEqual LATIN1
      userCanConnect("auser", "sa", 3310, "aSchema") must beTrue
    }
  }

  override protected def after: Any = if (mysqld != null) mysqld.stop()
}
