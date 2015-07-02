package com.wix.mysql

import java.lang

import com.wix.mysql.ScriptResolver.classPathFile
import com.wix.mysql.config.Charset.LATIN1
import com.wix.mysql.config.{MysqldConfig, SchemaConfig}
import com.wix.mysql.distribution.Version._
import org.specs2.matcher.MatchResult
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope
import org.springframework.jdbc.core.JdbcTemplate

import scala.util.Try

/**
 * @author viliusl
 * @since 28/05/15
 */
class EmbeddedMysqldTest extends SpecWithJUnit {
  sequential

  trait Context extends Scope {
    var mysqld: EmbeddedMysql = _

    def verifySchema(mysqldConfig: MysqldConfig, schemaConfig: SchemaConfig): MatchResult[Any] = {
      new JdbcTemplate(Datasource.`with`(mysqldConfig, schemaConfig))
        .queryForObject("select col1 from t1;", classOf[lang.Long]) mustEqual 10
    }

    def verifySchema(mysqldConfig: MysqldConfig, schemaName: String): MatchResult[Any] = {
      new JdbcTemplate(Datasource.`with`(mysqldConfig, schemaName))
        .queryForObject("select col1 from t1;", classOf[lang.Long]) mustEqual 10
    }
  }

  "EmbeddedMysql can be run with " >> {

    "default configuration and a single schema provided via instance builder" in new Context {
      try {
        mysqld = EmbeddedMysql.Builder(v5_6_latest)
          .addSchema("aschema", classPathFile("db/001_init.sql"))
          .start

        verifySchema(mysqld.getConfig, "aschema")

      } finally Try { mysqld.stop() }
    }

    "default configuration with custom version and a single schema provided via instance builder" in new Context {
      try {
        mysqld = EmbeddedMysql.Builder(v5_5_40)
          .addSchema("aschema", classPathFile("db/001_init.sql"))
          .start

        verifySchema(mysqld.getConfig, "aschema")

      } finally Try { mysqld.stop() }
    }

    "MysqldConfig and a single schema provided via instance builder" in new Context {
      try {
        val config = MysqldConfig.Builder(v5_6_latest)
          .withPort(1120)
          .withCharset(LATIN1)
          .withUser("someuser", "somepassword")
          .build()

        mysqld = EmbeddedMysql.Builder(config)
          .addSchema("aschema", classPathFile("db/001_init.sql"))
          .start

        verifySchema(mysqld.getConfig, "aschema")

      } finally Try { mysqld.stop() }
    }

    "schema config" in new Context {
      try {
        val schema = SchemaConfig.Builder("aschema")
          .withScripts(classPathFile("db/001_init.sql"))
          .build

        mysqld = EmbeddedMysql.Builder(v5_5_40)
          .addSchema(schema)
          .start

        verifySchema(mysqld.getConfig, schema)

      } finally Try { mysqld.stop() }
    }

    "multiple schemas" in new Context {
      try {
        mysqld = EmbeddedMysql.Builder(v5_5_40)
          .addSchema("aschema", classPathFile("db/001_init.sql"))
          .addSchema("aschema2", classPathFile("db/001_init.sql"))
          .start

        verifySchema(mysqld.getConfig, "aschema")
        verifySchema(mysqld.getConfig, "aschema2")

      } finally Try { mysqld.stop() }
    }

    "schema added after EmbeddedMysql start-up" in new Context {
      try {
        mysqld = EmbeddedMysql.Builder(v5_6_latest).start()
        mysqld.addSchema("aschema", classPathFile("db/001_init.sql"))

        verifySchema(mysqld.getConfig, "aschema")

      } finally Try { mysqld.stop() }
    }

//    //TODO: verify that charset provided is actual or db
//    //TODO: verify that charset provided for instance is actual
//    //TODO: multiple schemas
 }
}