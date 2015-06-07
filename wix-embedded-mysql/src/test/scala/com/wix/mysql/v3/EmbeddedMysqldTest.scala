package com.wix.mysql.v3

import java.io.File
import java.lang
import javax.sql.DataSource

import com.wix.mysql.{EmbeddedMysql, ClassPathScriptResolver}
import com.wix.mysql.config.{MysqldConfig, SchemaConfig}
import com.wix.mysql.distribution.Version
import com.wix.mysql.distribution.Version._
import org.specs2.execute.{Result, AsResult}
import org.specs2.matcher.MatchResult
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.{ForEach, Scope}
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

    def verifySchema(schemaConfig: SchemaConfig): MatchResult[Any] = {
      new JdbcTemplate(mysqld.dataSourceFor(schemaConfig))
        .queryForObject("select col1 from t1;", classOf[lang.Long]) mustEqual 10
    }
  }

  "MysqldConfig" should {

    //TODO: verify that charset provided is actual or db
    //TODO: verify that charset provided for instance is actual
    "basic" in new Context {
      try {
        val schemaConfig = SchemaConfig.Builder("aschema")
          .withScripts(ClassPathScriptResolver.file("db/001_init.sql"))
          .build()

        mysqld = EmbeddedMysql.Builder(v5_6_latest)
          .addSchema(schemaConfig)
          .start()

        verifySchema(schemaConfig)

      } finally Try { mysqld.stop() }
    }

    "basic - with custom user" in new Context {
      try {
        val schemaConfig = SchemaConfig.Builder("aschema")
          .withScripts(ClassPathScriptResolver.file("db/001_init.sql"))
          .build()

        mysqld = EmbeddedMysql.Builder(v5_6_latest)
          .withUser("second", "za")
          .addSchema(schemaConfig)
          .start()

        verifySchema(schemaConfig)

      } finally Try { mysqld.stop() }
    }

    "basic with custom port" in new Context {
      try {
        val schemaConfig = SchemaConfig.Builder("aschema")
          .withScripts(ClassPathScriptResolver.file("db/001_init.sql"))
          .build()

        mysqld = EmbeddedMysql.Builder(v5_6_latest, 1111)
          .addSchema(schemaConfig)
          .start()

        verifySchema(schemaConfig)

      } finally Try { mysqld.stop() }
    }

    "with MysqldConfig" in new Context {
      try {
        val config = MysqldConfig.Builder(v5_6_latest).build()
        val schemaConfig = SchemaConfig.Builder("aschema")
          .withScripts(ClassPathScriptResolver.file("db/001_init.sql"))
          .build()

        mysqld = EmbeddedMysql.Builder(config)
          .addSchema(schemaConfig)
          .start()

        verifySchema(schemaConfig)

      } finally Try { mysqld.stop() }
    }

    "add schema after start" in new Context {
      try {
        mysqld = EmbeddedMysql.Builder(v5_6_latest).start()

        val schemaConfig = SchemaConfig.defaults("aschema")

        mysqld.addSchema(schemaConfig)
        mysqld.apply(schemaConfig, ClassPathScriptResolver.file("db/001_init.sql"))

        verifySchema(schemaConfig)

      } finally Try { mysqld.stop() }
    }
  }

}