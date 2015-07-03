package com.wix.mysql

import java.lang

import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.ScriptResolver.classPathFile
import com.wix.mysql.config.Charset.LATIN1
import com.wix.mysql.config.MysqldConfig.aMysqldConfig
import com.wix.mysql.config.SchemaConfig.aSchemaConfig
import com.wix.mysql.distribution.Version._
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.AfterEach
import org.springframework.jdbc.core.JdbcTemplate

import scala.reflect.{ClassTag, classTag}

/**
 * @author viliusl
 * @since 28/05/15
 */
class UsageExamplesTest extends SpecWithJUnit with AfterEach {
  sequential

  var mysqld: EmbeddedMysql = _

  override protected def after: Any = if (mysqld != null) mysqld.stop()

  def aSelect[T: ClassTag](onSchema: String, sql: String): T =
    new JdbcTemplate(Datasource.aDataSource(mysqld.getConfig, aSchemaConfig(onSchema).build))
      .queryForObject("select col1 from t1;", classTag[T].runtimeClass.asInstanceOf[Class[T]])

  def verifySchema(schema: String, withScript: String) = {
    withScript match {
      case "db/001_init.sql" => aSelect[lang.Long](onSchema = "aschema", sql = "select col1 from t1;") mustEqual 10
    }
  }

  "EmbeddedMysql can be run with " >> {

    "default configuration and a single schema provided via instance builder" in {
      mysqld = anEmbeddedMysql(v5_6_latest)
        .addSchema("aschema", classPathFile("db/001_init.sql"))
        .start

      verifySchema("aschema", withScript = "db/001_init.sql")
    }

    "default configuration with custom version and a single schema provided via instance builder" in {
      mysqld = anEmbeddedMysql(v5_5_40)
        .addSchema("aschema", classPathFile("db/001_init.sql"))
        .start

      verifySchema("aschema", withScript = "db/001_init.sql")
    }

    "MysqldConfig and a single schema provided via instance builder" in {
      val config = aMysqldConfig(v5_6_latest)
        .withPort(1120)
        .withCharset(LATIN1)
        .withUser("someuser", "somepassword")
        .build()

      mysqld = anEmbeddedMysql(config)
        .addSchema("aschema", classPathFile("db/001_init.sql"))
        .start

      verifySchema("aschema", withScript = "db/001_init.sql")
    }

    "schema config" in {
      val schema = aSchemaConfig("aschema")
        .withScripts(classPathFile("db/001_init.sql"))
        .build

      mysqld = anEmbeddedMysql(v5_5_40)
        .addSchema(schema)
        .start

      verifySchema("aschema", withScript = "db/001_init.sql")
    }

    "multiple schemas" in {
      mysqld = anEmbeddedMysql(v5_5_40)
        .addSchema("aschema", classPathFile("db/001_init.sql"))
        .addSchema("aschema2", classPathFile("db/001_init.sql"))
        .start

      verifySchema("aschema", withScript = "db/001_init.sql")
      verifySchema("aschema2", withScript = "db/001_init.sql")
    }

    "schema added after EmbeddedMysql start-up" in {
      mysqld = anEmbeddedMysql(v5_6_latest).start()
      mysqld.addSchema("aschema", classPathFile("db/001_init.sql"))

      verifySchema("aschema", withScript = "db/001_init.sql")
    }
  }
}