package com.wix.mysql

import java.lang.String._
import javax.sql.DataSource

import com.wix.mysql.EmbeddedMysql._
import com.wix.mysql.config.{SchemaConfig, MysqldConfig}
import com.wix.mysql.distribution.Version
import org.apache.commons.dbcp2.BasicDataSource
import org.specs2.specification.{AfterEach, Scope}
import org.springframework.jdbc.core.JdbcTemplate

import scala.reflect._

/**
 * @author viliusl
 * @since 03/07/15
 */
class EmbeddedMysqlTest extends IntegrationTest with AfterEach {
  var mysqld: EmbeddedMysql = _

  trait Context extends Scope {

    def systemDataSource(config: MysqldConfig): DataSource = {
      val dataSource: BasicDataSource = new BasicDataSource
      dataSource.setDriverClassName("com.mysql.jdbc.Driver")
      dataSource.setUrl(s"jdbc:mysql://localhost:${config.getPort}/information_schema")
      dataSource.setUsername("root")
      dataSource
    }

    def aSelect[T: ClassTag](onSchema: String, sql: String): T =
      new JdbcTemplate(Datasource.`with`(mysqld.getConfig, SchemaConfig.Builder(onSchema).build))
        .queryForObject("select col1 from t1;", classTag[T].runtimeClass.asInstanceOf[Class[T]])


    def serverCharsetFor(mysqld: EmbeddedMysql): String = {
      new JdbcTemplate(systemDataSource(mysqld.getConfig))
        .queryForObject("SELECT variable_value FROM information_schema.global_variables WHERE variable_name = 'character_set_server';", classOf[String])
    }
  }

  "EmbeddedMysql" should {
    "use values from MysqldConfig" in new Context {
      val config = MysqldConfig.Builder(Version.v5_6_latest).build

      mysqld = anEmbeddedMysql(config).start

      serverCharsetFor(mysqld) mustEqual config.getCharset
    }
  }

  override protected def after: Any = if (mysqld != null) mysqld.stop
}
