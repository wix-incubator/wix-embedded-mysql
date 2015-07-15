package com.wix.mysql

import javax.sql.DataSource

import ch.qos.logback.classic.Level.INFO
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.{Logger, LoggerContext}
import ch.qos.logback.core.read.ListAppender
import com.wix.mysql.config.MysqldConfig
import org.apache.commons.dbcp2.BasicDataSource
import org.slf4j.LoggerFactory
import org.slf4j.LoggerFactory.getLogger
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.AfterEach
import org.springframework.jdbc.core.JdbcTemplate

import scala.collection.JavaConversions._
import scala.reflect._

/**
 * @author viliusl
 * @since 27/03/15
 */
class IntegrationTest extends SpecWithJUnit with AfterEach {
  sequential

  var mysqld: EmbeddedMysql = _
  val log = getLogger(this.getClass)

  override protected def after: Any = if (mysqld != null) mysqld.stop()

  def validateConnection(config: MysqldConfig, schema: String) =
    new JdbcTemplate(aDataSource(config, schema))
      .queryForObject("select 1;", classOf[java.lang.Long]) mustEqual 1

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

  def aSelect[T: ClassTag](ds: DataSource, sql: String): T =
    new JdbcTemplate(ds).queryForObject(sql, classTag[T].runtimeClass.asInstanceOf[Class[T]])

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
      def iterator = appender.list map ( _.getMessage ) iterator
    }
  }
}