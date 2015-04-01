package com.wix.mysql

import java.util.logging.{Level, Logger, LogManager}

import com.mchange.v2.c3p0.ComboPooledDataSource
import com.wix.mysql.IntegrationTest._
import com.wix.mysql.config.MysqldConfig
import org.slf4j.bridge.SLF4JBridgeHandler
import org.specs2.mutable.{Before, SpecificationWithJUnit}
import org.springframework.jdbc.core.JdbcTemplate

/**
 * @author viliusl
 * @since 27/03/15
 */
class IntegrationTest extends SpecificationWithJUnit with Before {
  sequential

  def before: Any = init

  def givenMySqlWithConfig(config: MysqldConfig) = MysqldStarter.defaultInstance.prepare(config)

  def startAndVerifyDatabase(config: MysqldConfig) = startAndVerify(config) {
      config.getSchemas foreach (validateConnection(config, _))
    }

  def startAndVerify(config: MysqldConfig)(verify: => Unit) = {
    val executable: MysqldExecutable = givenMySqlWithConfig(config)
    try {
      executable.start()
      verify
    } finally executable.stop()
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

  def connectionUrlFor(config: MysqldConfig, schema: String) = s"jdbc:mysql://localhost:${config.getPort}/$schema"

}

object IntegrationTest {
  lazy val init = {
    LogManager.getLogManager.reset
    SLF4JBridgeHandler.install
    Logger.getLogger("global").setLevel(Level.FINEST)
  }
}
