package com.wix.mysql

import com.wix.mysql.config.MysqldConfig.SystemDefaults._
import com.wix.mysql.config.{MysqldConfig, MysqldConfigBuilder}
import com.wix.mysql.distribution.Version._
import org.specs2.specification.Scope
import org.springframework.jdbc.core.JdbcTemplate

import scala.collection.convert.decorateAsScala._

/**
 * @author viliusl
 * @since 26/03/15
 */
class CustomConfigurationTest extends IntegrationTest {

  trait Context extends Scope {
    val template = new MysqldConfigBuilder(v5_6_21)
  }

  "embedded mysql should run with" >> {

    "defaults" in new Context {
      val config = template.build
      startAndVerifyDatabase(config)
    }

    "custom config" in new Context {
      val config = template.withUsername("auser").withPassword("sa").withSchema("some_schema").withPort(9913).build
      startAndVerifyDatabase(config)
    }

    "two schemas" in new Context {
      val config = template.withUsername("auser").withPassword("sa").withSchemas(Array("schema1", "schema2")).withPort(9913).build

      startAndVerify(config) {
        val res = new JdbcTemplate(dataSourceFor(config, SCHEMA)).queryForList[String]("SHOW DATABASES;", classOf[String]).asScala
        res must contain("schema1", "schema2")
      }
    }

    "system user and custom schema" in new Context {
      val config = template.withUsername(USERNAME).withPassword(PASSWORD).withSchema(MysqldConfig.Defaults.SCHEMA + "a").build
      startAndVerifyDatabase(config)
    }

    "null password" in new Context {
      val config = template.withPassword(null).build
      startAndVerifyDatabase(config)
    }
  }
}
