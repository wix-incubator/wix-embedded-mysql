package com.wix.mysql.v2

import java.io.File
import javax.sql.DataSource

import com.wix.mysql.config.MysqldConfig
import com.wix.mysql.distribution.Version
import com.wix.mysql.distribution.Version.v5_6_latest
import org.specs2.mutable.SpecWithJUnit
import org.springframework.jdbc.core.JdbcTemplate

/**
 * @author viliusl
 * @since 28/05/15
 */
class MysqldTest extends SpecWithJUnit {

  "simple" in {
    val config = MysqldConfig.Builder(v5_6_latest).build()
    val (username, password, schema) = ("auser", "sa", "aschema")
    val mysqld = Mysqld.start(config)

    mysqld
      .addUser(username, password)
      .addSchema(schema)
      .addGrant(schema, username)

    mysqld.client(schema).executeScript(new File(getClass.getResource("sql-scripts/init_schema.sql").toURI()))

    new JdbcTemplate(dataSourceFor(schema, config.getPort, username, password))
      .queryForObject("select 1;", classOf[java.lang.Long]) mustEqual 1

    mysqld.stop()
  }

  "with mgrations" in {
    val config = MysqldConfig.Builder(Version.v5_6_latest).build()
    val (username, password, schema) = ("auser", "sa", "aschema")

    val mysqld = Mysqld.start(config)
      .addUser(username, password)
      .addSchema(schema)
      .addGrant(schema, username)

    mysqld.client(schema).apply(ClassPathMigration.forPattern("scripts/*.sql"))

    new JdbcTemplate(dataSourceFor(schema, config.getPort, username, password))
      .queryForObject("select 1;", classOf[java.lang.Long]) mustEqual 1

    mysqld.stop()
  }

  "with mgrations" in {
    val config = MysqldConfig.Builder(v5_6_latest).build()
    val (username, password, schema) = ("auser", "sa", "aschema")

    val mysqld = Mysqld.start(config)

    mysqld
      .addUser(username, password)
      .addSchema(schema)
      .addGrant(schema, username)

    mysqld.client(schema).apply(ClassPathMigration.forPattern("scripts/*.sql"))

    new JdbcTemplate(dataSourceFor(schema, config.getPort, username, password))
      .queryForObject("select 1;", classOf[java.lang.Long]) mustEqual 1

    mysqld.stop()
  }

  def dataSourceFor(schema: String, port: Int, user: String, password: String): DataSource = ???
}

class MysqlClient(schema: String) {
  def executeSql(scripts: String*): Unit = ???
  def executeScript(files: File*): Unit = ???
  def apply(files: Migration*): Unit = ???
}

class Mysqld {
  def client(schema: String): MysqlClient = ???

  def addUser(username: String, password: String): Mysqld = ???

  def addSchema(name: String): Mysqld = ???
  def addSchema(name: String, charset: Charset): Mysqld = ???

  def addGrant(schema: String, username: String): Mysqld = ???

  def stop(): Unit = ???
}

object Mysqld {
  def start(version: Version, port: Int): Mysqld = ???
  def start(config: MysqldConfig): Mysqld = ???
}

trait Migration

trait FileMigration extends Migration {
  def apply(client: MysqlClient)
}

trait JdbcMigration extends Migration {
  def apply(ds: DataSource)
}

class Charset {
}

class ClassPathMigration extends FileMigration {
  override def apply(client: MysqlClient): Unit = ???
}

object ClassPathMigration {
  def forPattern(paths: String): ClassPathMigration = ???
}