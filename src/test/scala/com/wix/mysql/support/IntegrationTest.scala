package com.wix.mysql.support

import javax.sql.DataSource

import ch.qos.logback.classic.Level.INFO
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.{Logger, LoggerContext}
import ch.qos.logback.core.read.ListAppender
import com.wix.mysql.config.MysqldConfig
import com.wix.mysql.{EmbeddedMysql, Sources, SqlScriptSource}
import org.apache.commons.dbcp2.BasicDataSource
import org.slf4j.LoggerFactory
import org.slf4j.LoggerFactory.getLogger
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.BeforeAfterEach
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.JdbcTemplate

import scala.collection.JavaConversions._
import scala.reflect._

abstract class IntegrationTest extends SpecWithJUnit with BeforeAfterEach
  with InstanceMatchers with TestResourceSupport with JdbcSupport {

  sequential

  var mysqldInstances: Seq[EmbeddedMysql] = Seq()
  val log = getLogger(this.getClass)

  def before: Any = {
    println("before")
    mysqldInstances = Seq()
  }
  def after: Any = {
    println("after")
    mysqldInstances.foreach(_.stop())
  }

  def withStop(mysqld: EmbeddedMysql): EmbeddedMysql = {
    mysqldInstances = mysqldInstances :+ mysqld
    mysqld
  }

  def aLogFor(app: String): Iterable[String] = {
    val appender: ListAppender[ILoggingEvent] = new ListAppender[ILoggingEvent]
    val context: LoggerContext = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]

    val logger: Logger = context.getLogger(app)

    logger.setAdditive(false)
    logger.setLevel(INFO)
    logger.detachAppender(appender.getName)
    logger.addAppender(appender)

    appender.start()

    new Iterable[String] {
      def iterator = appender.list map (_.getMessage) iterator
    }
  }
}

trait JdbcSupport {
  self: IntegrationTest =>

  def aDataSource(config: MysqldConfig, schema: String): DataSource =
    aDataSource(config.getUsername, config.getPassword, config.getPort, schema)

  def aDataSource(user: String, password: String, port: Int, schema: String): DataSource = {
    val dataSource: BasicDataSource = new BasicDataSource
    dataSource.setDriverClassName("com.mysql.jdbc.Driver")
    dataSource.setUrl(s"jdbc:mysql://localhost:$port/$schema")
    dataSource.setUsername(user)
    dataSource.setPassword(password)
    dataSource
  }

  def aJdbcTemplate(mysqld: EmbeddedMysql, forSchema: String): JdbcTemplate =
    new JdbcTemplate(aDataSource(mysqld.getConfig, forSchema))

  def aSelect[T: ClassTag](ds: DataSource, sql: String): T =
    new JdbcTemplate(ds).queryForObject(sql, classTag[T].runtimeClass.asInstanceOf[Class[T]])

  def aSelect[T: ClassTag](mysqld: EmbeddedMysql, onSchema: String, sql: String): T =
    aJdbcTemplate(mysqld, onSchema).queryForObject(sql, classTag[T].runtimeClass.asInstanceOf[Class[T]])

  def aQuery(mysqld: EmbeddedMysql, onSchema: String, sql: String): Unit =
    aJdbcTemplate(mysqld, forSchema = onSchema).execute(sql)

  def anUpdate(mysqld: EmbeddedMysql, onSchema: String, sql: String): Unit =
    aJdbcTemplate(mysqld, forSchema = onSchema).execute(sql)

  def aMigrationWith(sql: String): SqlScriptSource = Sources.fromFile(createTempFile(sql))

  def beSuccessful = not(throwAn[Exception])

  def failWith(fragment: String) = throwA[DataAccessException].like { case e =>
    e.getMessage must contain(fragment)
  }

}