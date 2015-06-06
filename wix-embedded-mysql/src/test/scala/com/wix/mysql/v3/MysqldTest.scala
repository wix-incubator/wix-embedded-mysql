package com.wix.mysql.v3

import java.io.File
import java.util.regex.Pattern
import javax.sql.DataSource

import com.wix.mysql.config.MysqldConfig
import com.wix.mysql.distribution.Version
import com.wix.mysql.distribution.Version.v5_6_latest
import org.specs2.mutable.SpecWithJUnit
import org.springframework.jdbc.core.JdbcTemplate
import sun.security.util.Password

/**
 * @author viliusl
 * @since 28/05/15
 */
class MysqldTest extends SpecWithJUnit {

  "basic" in {
    val schemaConfig = SchemaConfig.Builder("aschema")
      .addScripts(ClassPathScriptResolver.file("db/init.sql"))
      .build()

    val mysqld = EmbeddedMysql.Builder(v5_6_latest)
      .addSchema(schemaConfig)
      .start()

    new JdbcTemplate(mysqld.dataSourceFor(schemaConfig))
      .queryForObject("select 1;", classOf[java.lang.Long]) mustEqual 1

    mysqld.stop()
  }

  "basic - with user" in {
    val schemaConfig = SchemaConfig.Builder("aschema")
      .withCharset(Charset.default())
      .addScripts(ClassPathScriptResolver.file("db/init.sql"))
      .build()

    val mysqld = EmbeddedMysql.Builder(v5_6_latest)
      .withUser("auser", "sa")
      .addSchema(schemaConfig)
      .start()

    new JdbcTemplate(mysqld.dataSourceFor(schemaConfig))
      .queryForObject("select 1;", classOf[java.lang.Long]) mustEqual 1

    mysqld.stop()
  }

  "basic with custom port" in {
    val schemaConfig = SchemaConfig.Builder("aschema")
      .addScripts(ClassPathScriptResolver.file("db/init.sql"))
      .build()

    val mysqld = EmbeddedMysql.Builder(v5_6_latest, 3310)
      .addSchema(schemaConfig)
      .start()

    new JdbcTemplate(mysqld.dataSourceFor(schemaConfig))
      .queryForObject("select 1;", classOf[java.lang.Long]) mustEqual 1

    mysqld.stop()
  }

  "with basic schema and script post-apply" in {
    val schemaConfig = SchemaConfig.defaults("aschema")

    val mysqld = EmbeddedMysql.Start(v5_6_latest)

    mysqld.createSchema(schemaConfig)
      .executeScript(ClassPathScriptResolver.file("db/init.sql"))

    new JdbcTemplate(mysqld.dataSourceFor(schemaConfig))
      .queryForObject("select 1;", classOf[java.lang.Long]) mustEqual 1

    mysqld.stop()
  }

  "obtaining a client" in {
    val schemaConfig = SchemaConfig.defaults("aschema")

    val mysqld = EmbeddedMysql.Start(v5_6_latest)
      .createSchema(schemaConfig)

//    mysqld
//      .executeScript(ClassPathScriptResolver.file("db/init.sql"))

    new JdbcTemplate(mysqld.dataSourceFor(schemaConfig))
      .queryForObject("select 1;", classOf[java.lang.Long]) mustEqual 1

    mysqld.stop()
  }



  "with MysqldConfig" in {
    val config = MysqldConfig.Builder(v5_6_latest).build()
    val schemaConfig = SchemaConfig.Builder("aschema")
      .addScripts(ClassPathScriptResolver.pattern("db/*.sql"))
      .build()

    val mysqld = EmbeddedMysql.Builder(config)
      .addSchema(schemaConfig)
      .start()

    new JdbcTemplate(mysqld.dataSourceFor(schemaConfig))
      .queryForObject("select 1;", classOf[java.lang.Long]) mustEqual 1

    mysqld.stop()
  }

}

class MysqlClient(schema: String) {
  def executeSql(scripts: String*): Unit = ???
  def executeScript(files: Seq[File]): Unit = ???
}

class Charset {
}

object Charset {
  def default(): Charset = ???
}

class SchemaConfig {
}

object SchemaConfig {
  def defaults(name: String): SchemaConfig = new Builder("name").build()

  def Builder(name: String): Builder = new Builder(name)

  class Builder(name: String) {
    def withCharset(charset: Charset): Builder = ???
    def addScripts(files: Seq[File]): Builder = ???
    def build() = new SchemaConfig()
  }
}

object ClassPathScriptResolver {
  def file(s: String): Seq[File] = ???
  def pattern(pattern: String): Seq[File] = ???
}

class EmbeddedMysql {
  def createSchema(schemaConfig: SchemaConfig): MysqlClient = ???

  def stop() : Unit = ???
  def dataSourceFor(schemaConfig: SchemaConfig): DataSource = ???
}

object EmbeddedMysql {
  def Start(version: Version): EmbeddedMysql = ???

  def Builder(version: Version, port: Int): Builder = ???
  def Builder(version: Version): Builder = ???
  def Builder(config: MysqldConfig): Builder = ???

  class Builder {
    def withUser(username: String, password: String): Builder = ???
    def addSchema(schemaConfig: SchemaConfig): Builder = ???
    def start(): EmbeddedMysql = ???
  }
}