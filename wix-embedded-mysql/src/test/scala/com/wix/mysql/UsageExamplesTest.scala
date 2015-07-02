package com.wix.mysql

import java.lang

import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.ScriptResolver.classPathFile
import com.wix.mysql.config.Charset.LATIN1
import com.wix.mysql.config.{MysqldConfig, SchemaConfig}
import com.wix.mysql.distribution.Version._
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope
import org.springframework.jdbc.core.JdbcTemplate

import scala.reflect.{classTag, ClassTag}
import scala.util.Try

/**
 * @author viliusl
 * @since 28/05/15
 */
class UsageExamplesTest extends SpecWithJUnit {
  sequential

  trait Context extends Scope {
    var mysqld: EmbeddedMysql = _

    def aSelect[T: ClassTag](onSchema: String, sql: String): T =
      new JdbcTemplate(Datasource.`with`(mysqld.getConfig, SchemaConfig.Builder(onSchema).build))
        .queryForObject("select col1 from t1;", classTag[T].runtimeClass.asInstanceOf[Class[T]])

    def verifySchema(schema: String, withScript: String) = {
      withScript match {
        case "db/001_init.sql" => aSelect[lang.Long](onSchema = "aschema", sql = "select col1 from t1;") mustEqual 10
      }
    }
  }

  "EmbeddedMysql can be run with " >> {

    "default configuration and a single schema provided via instance builder" in new Context {
      try {
        mysqld = anEmbeddedMysql(v5_6_latest)
          .addSchema("aschema", classPathFile("db/001_init.sql"))
          .start

        verifySchema("aschema", withScript = "db/001_init.sql")

      } finally Try { mysqld.stop() }
    }

    "default configuration with custom version and a single schema provided via instance builder" in new Context {
      try {
        mysqld = anEmbeddedMysql(v5_5_40)
          .addSchema("aschema", classPathFile("db/001_init.sql"))
          .start

        verifySchema("aschema", withScript = "db/001_init.sql")

      } finally Try { mysqld.stop() }
    }

    "MysqldConfig and a single schema provided via instance builder" in new Context {
      try {
        val config = MysqldConfig.aMysqldConfig(v5_6_latest)
          .withPort(1120)
          .withCharset(LATIN1)
          .withUser("someuser", "somepassword")
          .build()

        mysqld = EmbeddedMysql.anEmbeddedMysql(config)
          .addSchema("aschema", classPathFile("db/001_init.sql"))
          .start

        verifySchema("aschema", withScript = "db/001_init.sql")

      } finally Try { mysqld.stop() }
    }

    "schema config" in new Context {
      try {
        val schema = SchemaConfig.Builder("aschema")
          .withScripts(classPathFile("db/001_init.sql"))
          .build

        mysqld = anEmbeddedMysql(v5_5_40)
          .addSchema(schema)
          .start

        verifySchema("aschema", withScript = "db/001_init.sql")

      } finally Try { mysqld.stop() }
    }

    "multiple schemas" in new Context {
      try {
        mysqld = anEmbeddedMysql(v5_5_40)
          .addSchema("aschema", classPathFile("db/001_init.sql"))
          .addSchema("aschema2", classPathFile("db/001_init.sql"))
          .start

        verifySchema("aschema", withScript = "db/001_init.sql")
        verifySchema("aschema2", withScript = "db/001_init.sql")

      } finally Try { mysqld.stop() }
    }

    "schema added after EmbeddedMysql start-up" in new Context {
      try {
        mysqld = anEmbeddedMysql(v5_6_latest).start()
        mysqld.addSchema("aschema", classPathFile("db/001_init.sql"))

        verifySchema("aschema", withScript = "db/001_init.sql")

      } finally Try { mysqld.stop() }
    }

//    //TODO: verify that charset provided is actual or db
//    //TODO: verify that charset provided for instance is actual
//    //TODO: multiple schemas
 }
}