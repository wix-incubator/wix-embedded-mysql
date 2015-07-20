package com.wix.mysql.support

import com.wix.mysql.EmbeddedMysql
import com.wix.mysql.config.{MysqldConfig, Charset}
import com.wix.mysql.config.Charset._
import com.wix.mysql.config.MysqldConfig.SystemDefaults
import org.specs2.matcher.Matchers

/**
 * @author viliusl
 * @since 20/07/15
 */
trait InstanceMatchers extends Matchers { self: IntegrationTest =>

  def haveCharsetOf(charset: Charset) =
    ===(charset) ^^ { mySql: EmbeddedMysql =>
      val ds = aDataSource(mysqld.getConfig, SystemDefaults.SCHEMA)
      aCharset(
        aSelect[String](ds, sql = "SELECT variable_value FROM information_schema.global_variables WHERE variable_name = 'character_set_server';"),
        aSelect[String](ds, sql = "SELECT variable_value FROM information_schema.global_variables WHERE variable_name = 'collation_server';"))
    }

  def haveSchemaCharsetOf(charset: Charset, onSchema: String) =
    ===(charset) ^^ { mySql: EmbeddedMysql =>
        val ds = aDataSource(mysqld.getConfig, onSchema)
        aCharset(
          aSelect[String](ds, sql = s"SELECT default_character_set_name FROM information_schema.SCHEMATA where SCHEMA_NAME = '$onSchema';"),
          aSelect[String](ds, sql = s"SELECT DEFAULT_COLLATION_NAME FROM information_schema.SCHEMATA where SCHEMA_NAME = '$onSchema';"))
    }

  def beAvailableOn(port: Int, withUser: String, password: String, andSchema: String = "information_schema") =
    beTrue ^^ { mySql: EmbeddedMysql =>
      aSelect[java.lang.Long](aDataSource(withUser, password, port, andSchema), "select 1;") == 1
    }

  def beAvailableOn(config: MysqldConfig, andSchema: String = "information_schema") =
    beAvailableOn(config.getPort, config.getUsername, config.getPassword, andSchema)

}
