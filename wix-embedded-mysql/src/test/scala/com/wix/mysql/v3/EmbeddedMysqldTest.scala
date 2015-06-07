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
class EmbeddedMysqldTest extends SpecWithJUnit {
  sequential

  trait Context extends Scope {
    var mysqld: EmbeddedMysql = null
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

        new JdbcTemplate(mysqld.dataSourceFor(schemaConfig))
          .queryForObject("select col1 from t1;", classOf[java.lang.Long]) mustEqual 10

      } finally {
        if (mysqld != null) mysqld.stop()
      }
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

        new JdbcTemplate(mysqld.dataSourceFor(schemaConfig))
          .queryForObject("select col1 from t1;", classOf[java.lang.Long]) mustEqual 10

      } finally {
        if (mysqld != null) mysqld.stop()
      }
    }

//    "basic with custom port" in {
//      val schemaConfig = SchemaConfig.Builder("aschema")
//        .withScripts(ClassPathScriptResolver.file("db/init.sql"))
//        .build()
//
//      val mysqld = EmbeddedMysql.Builder(v5_6_latest, 3310)
//        .addSchema(schemaConfig)
//        .start()
//
//      new JdbcTemplate(mysqld.dataSourceFor(schemaConfig))
//        .queryForObject("select 1;", classOf[java.lang.Long]) mustEqual 1
//
//      mysqld.stop()
//
//      success
//    }
//
//    "with MysqldConfig" in {
//      val config = MysqldConfig.Builder(v5_6_latest).build()
//      val schemaConfig = SchemaConfig.Builder("aschema")
//        .withScripts(ClassPathScriptResolver.file("db/init.sql"))
//        .build()
//
//      val mysqld = EmbeddedMysql.Builder(config)
//        .addSchema(schemaConfig)
//        .start()
//
//      new JdbcTemplate(mysqld.dataSourceFor(schemaConfig))
//        .queryForObject("select 1;", classOf[java.lang.Long]) mustEqual 1
//
//      mysqld.stop()
//
//      success
//    }
//
//
//    "with basic schema and script post-executeCommands" in {
//      val schemaConfig = SchemaConfig.defaults("aschema")
//
//      val mysqld = EmbeddedMysql.Builder(v5_6_latest).start()
//
//      mysqld.addSchema(schemaConfig)
//      mysqld.executeCommands(schemaConfig, ClassPathScriptResolver.file("db/init.sql"))
//
//      new JdbcTemplate(mysqld.dataSourceFor(schemaConfig))
//        .queryForObject("select 1;", classOf[java.lang.Long]) mustEqual 1
//
//      mysqld.stop()
//
//      success
//    }
  }
}