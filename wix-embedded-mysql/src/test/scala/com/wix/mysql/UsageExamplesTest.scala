package com.wix.mysql

import java.lang

import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.ScriptResolver.{classPathFile, classPathFiles}
import com.wix.mysql.config.Charset.LATIN1
import com.wix.mysql.config.MysqldConfig.aMysqldConfig
import com.wix.mysql.config.SchemaConfig.aSchemaConfig
import com.wix.mysql.distribution.Version._
import com.wix.mysql.support.IntegrationTest

/**
 * @author viliusl
 * @since 28/05/15
 */
class UsageExamplesTest extends IntegrationTest {

  def verifySchema(schema: String, withChangeSet: String) = {
    def ds = aDataSource(mysqld.getConfig, schema)
    withChangeSet match {
      case "db/001_init.sql" => aSelect[lang.Long](ds, sql = "select col1 from t1;") mustEqual 10
      case "db/*.sql" => aSelect[lang.Long](ds, sql = "select col1 from t3;") mustEqual 30
    }
  }

  "EmbeddedMysql can be run with " >> {

    "default configuration and a single schema provided via instance builder" in {
      mysqld = anEmbeddedMysql(v5_6_latest)
        .addSchema("aschema", classPathFile("db/001_init.sql"))
        .start

      verifySchema("aschema", withChangeSet = "db/001_init.sql")
    }

    "default configuration with custom version and a single schema provided via instance builder" in {
      mysqld = anEmbeddedMysql(v5_5_40)
        .addSchema("aschema", classPathFile("db/001_init.sql"))
        .start

      verifySchema("aschema", withChangeSet = "db/001_init.sql")
    }

    "MysqldConfig and a single schema provided via instance builder" in {
      val config = aMysqldConfig(v5_6_latest)
        .withPort(1120)
        .withCharset(LATIN1)
        .withUser("someuser", "somepassword")
        .build

      mysqld = anEmbeddedMysql(config)
        .addSchema("aschema", classPathFiles("db/*.sql"))
        .start

      verifySchema("aschema", withChangeSet = "db/*.sql")
    }

    "schema config" in {
      val schema = aSchemaConfig("aschema")
        .withScripts(classPathFile("db/001_init.sql"))
        .build

      mysqld = anEmbeddedMysql(v5_5_40)
        .addSchema(schema)
        .start

      verifySchema("aschema", withChangeSet = "db/001_init.sql")
    }

    "multiple schemas" in {
      mysqld = anEmbeddedMysql(v5_5_40)
        .addSchema("aschema", classPathFile("db/001_init.sql"))
        .addSchema("aschema2", classPathFiles("db/*.sql"))
        .start

      verifySchema("aschema", withChangeSet = "db/001_init.sql")
      verifySchema("aschema2", withChangeSet = "db/*.sql")
    }

    "reload schema for a running instance" in {
      todo
    }
  }
}