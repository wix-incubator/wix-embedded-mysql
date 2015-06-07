package com.wix.mysql.v3

import java.io.File
import javax.sql.DataSource

import com.wix.mysql.{EmbeddedMysql, ClassPathScriptResolver}
import com.wix.mysql.config.{MysqldConfig, SchemaConfig}
import com.wix.mysql.distribution.Version
import com.wix.mysql.distribution.Version._
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope
import org.springframework.jdbc.core.JdbcTemplate

/**
 * @author viliusl
 * @since 28/05/15
 */
class MysqldTest extends SpecWithJUnit {
  sequential

  trait Context extends Scope {
    var mysqld: EmbeddedMysql = null
  }

  "MysqldConfig" should {

    "basic" in new Context {
      try {
        val schemaConfig = SchemaConfig.Builder("aschema")
          .withScripts(ClassPathScriptResolver.file("db/001_init.sql"))
          .build()

        mysqld = EmbeddedMysql.Builder(v5_6_latest)
          .addSchema(schemaConfig)
          .start()

        new JdbcTemplate(mysqld.dataSourceFor(schemaConfig))
          .queryForObject("select 1;", classOf[java.lang.Long]) mustEqual 1

      } catch {
        case e => println(e); e.printStackTrace
      } finally mysqld.stop()
    }

    "basic - with custom user" in {
      val schemaConfig = SchemaConfig.Builder("aschema")
        .withScripts(ClassPathScriptResolver.file("db/init.sql"))
        .build()

      val mysqld = EmbeddedMysql.Builder(v5_6_latest)
        .withUser("auser", "sa")
        .addSchema(schemaConfig)
        .start()

      new JdbcTemplate(mysqld.dataSourceFor(schemaConfig))
        .queryForObject("select 1;", classOf[java.lang.Long]) mustEqual 1

      mysqld.stop()

      success
    }

    "basic with custom port" in {
      val schemaConfig = SchemaConfig.Builder("aschema")
        .withScripts(ClassPathScriptResolver.file("db/init.sql"))
        .build()

      val mysqld = EmbeddedMysql.Builder(v5_6_latest, 3310)
        .addSchema(schemaConfig)
        .start()

      new JdbcTemplate(mysqld.dataSourceFor(schemaConfig))
        .queryForObject("select 1;", classOf[java.lang.Long]) mustEqual 1

      mysqld.stop()

      success
    }

    "with MysqldConfig" in {
      val config = MysqldConfig.Builder(v5_6_latest).build()
      val schemaConfig = SchemaConfig.Builder("aschema")
        .withScripts(ClassPathScriptResolver.file("db/init.sql"))
        .build()

      val mysqld = EmbeddedMysql.Builder(config)
        .addSchema(schemaConfig)
        .start()

      new JdbcTemplate(mysqld.dataSourceFor(schemaConfig))
        .queryForObject("select 1;", classOf[java.lang.Long]) mustEqual 1

      mysqld.stop()

      success
    }


    "with basic schema and script post-apply" in {
      val schemaConfig = SchemaConfig.defaults("aschema")

      val mysqld = EmbeddedMysql.Builder(v5_6_latest).start()

      mysqld.addSchema(schemaConfig)
      mysqld.apply(schemaConfig, ClassPathScriptResolver.file("db/init.sql"))

      new JdbcTemplate(mysqld.dataSourceFor(schemaConfig))
        .queryForObject("select 1;", classOf[java.lang.Long]) mustEqual 1

      mysqld.stop()

      success
    }
  }
}