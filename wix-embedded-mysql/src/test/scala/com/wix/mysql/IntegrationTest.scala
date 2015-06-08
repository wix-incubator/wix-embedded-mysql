package com.wix.mysql

import ch.qos.logback.classic.Level.INFO
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.{Logger, LoggerContext}
import ch.qos.logback.core.read.ListAppender
import com.mchange.v2.c3p0.ComboPooledDataSource
import com.wix.mysql.config.MysqldConfig
import org.slf4j.LoggerFactory
import org.specs2.mutable.SpecWithJUnit
import org.springframework.jdbc.core.JdbcTemplate

import scala.collection.JavaConversions._

/**
 * @author viliusl
 * @since 27/03/15
 */
class IntegrationTest extends SpecWithJUnit {
  sequential

  def givenMySqlWithConfig(config: MysqldConfig) = MysqldStarter.defaultInstance.prepare(config)

  def startAndVerifyDatabase(config: MysqldConfig) = startAndVerify(config) {
      config.getSchemas foreach (validateConnection(config, _))
    }

  def startAndVerify(config: MysqldConfig)(verify: => Unit) = {
    val executable: MysqldExecutable = givenMySqlWithConfig(config)
    try {
      executable.start()
      verify
    } finally executable.stop
  }

  def validateConnection(config: MysqldConfig, schema: String) =
    new JdbcTemplate(dataSourceFor(config, schema))
      .queryForObject("select 1;", classOf[java.lang.Long]) mustEqual 1

  def dataSourceFor(config: MysqldConfig, schema: String) = {
    val cpds: ComboPooledDataSource = new ComboPooledDataSource
    cpds.setDriverClass("com.mysql.jdbc.Driver")
    cpds.setJdbcUrl(connectionUrlFor(config, schema))
    cpds.setUser(config.getUsername)
    cpds.setPassword(config.getPassword)
    cpds
  }

  def connectionUrlFor(config: MysqldConfig, schema: String) = s"jdbc:mysql://127.0.0.1:${config.getPort}/$schema"

  def aLogFor(app: String): Iterable[String] = {
    val appender: ListAppender[ILoggingEvent] = new ListAppender[ILoggingEvent]
    val context: LoggerContext = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]

    val logger: Logger = context.getLogger(app)

    logger.setAdditive(false)
    logger.setLevel(INFO)
    logger.detachAppender(appender.getName)
    logger.addAppender(appender)

    appender.start

    new Iterable[String] {
      def iterator = appender.list map ( _.getMessage ) iterator
    }
  }
}