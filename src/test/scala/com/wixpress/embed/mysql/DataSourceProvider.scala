package com.wixpress.embed.mysql

import javax.sql.DataSource

import com.wixpress.framework.javaconfig.jdbc.ComboPooledDataSourceTopology
import com.wixpress.framework.jdbc.DataSourceBuilder
import com.wixpress.framework.throttling.SeqBasedResourcePoolStatusReportingRegistry

/**
 * @author viliusl
 * @since 24/09/14
 */
object DataSourceProvider {

  def dataSourceFor(
    url: String = "jdbc:mysql://localhost:3306/information_schema",
    user: String = "root",
    password: String = null): DataSource = {

    val config = DataSourceConfig(username = user, password = password, url = url)

    DataSourceBuilder(config.comboPooledDataSource)
        .withResourcePoolStatusReportingRegistry(SeqBasedResourcePoolStatusReportingRegistry)
        .withName("sitePropertiesDataSource")
        .build
  }
}

case class DataSourceConfig(
                             username: String,
                             password: String,
                             url: String,
                             driverClassName: Option[String] = None,
                             minPoolSize: Option[Int] = None,
                             initialPoolSize: Option[Int] = None,
                             maxPoolSize: Option[Int] = None,
                             acquireIncrement: Option[Int] = None,
                             testConnectionOnCheckin: Option[Boolean] = None,
                             testConnectionOnCheckout: Option[Boolean] = None,
                             preferredTestQuery: Option[String] = None,
                             autoCommitOnClose: Option[Boolean] = None,
                             idleConnectionTestPeriod: Option[Int] = None,
                             maxIdleTime: Option[Int] = None,
                             maxIdleTimeExcessConnections: Option[Int] = None,
                             checkoutTimeout: Option[Int] = None,
                             numHelperThreads: Option[Int] = None,
                             unreturnedConnectionTimeout: Option[Int] = None,
                             debugUnreturnedConnectionStackTraces: Option[Boolean] = None) {self =>

  def comboPooledDataSource = new ComboPooledDataSourceTopology {
    val that = self
    val defaults = new ComboPooledDataSourceTopology {}

    username = that.username
    password = that.password
    url = that.url
    driverClassName = that.driverClassName.getOrElse(defaults.driverClassName)
    minPoolSize = that.minPoolSize.getOrElse(defaults.minPoolSize)
    initialPoolSize = that.initialPoolSize.getOrElse(defaults.initialPoolSize)
    maxPoolSize = that.maxPoolSize.getOrElse(defaults.maxPoolSize)
    acquireIncrement = that.acquireIncrement.getOrElse(defaults.acquireIncrement)
    testConnectionOnCheckin = that.testConnectionOnCheckin.getOrElse(defaults.testConnectionOnCheckin)
    testConnectionOnCheckout = that.testConnectionOnCheckout.getOrElse(defaults.testConnectionOnCheckout)
    preferredTestQuery = that.preferredTestQuery.getOrElse(defaults.preferredTestQuery)
    autoCommitOnClose = that.autoCommitOnClose.getOrElse(defaults.autoCommitOnClose)
    idleConnectionTestPeriod = that.idleConnectionTestPeriod.getOrElse(defaults.idleConnectionTestPeriod)
    maxIdleTime = that.maxIdleTime.getOrElse(defaults.maxIdleTime)
    maxIdleTimeExcessConnections = that.maxIdleTimeExcessConnections.getOrElse(defaults.maxIdleTimeExcessConnections)
    checkoutTimeout = that.checkoutTimeout.getOrElse(defaults.checkoutTimeout)
    numHelperThreads = that.numHelperThreads.getOrElse(defaults.numHelperThreads)
    unreturnedConnectionTimeout = that.unreturnedConnectionTimeout.getOrElse(defaults.unreturnedConnectionTimeout)
    debugUnreturnedConnectionStackTraces = that.debugUnreturnedConnectionStackTraces.getOrElse(defaults.debugUnreturnedConnectionStackTraces)
  }
}