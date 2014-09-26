package com.wixpress.embed.mysql

import javax.sql.DataSource

import com.mchange.v2.c3p0.ComboPooledDataSource

/**
 * @author viliusl
 * @since 24/09/14
 */
object DataSourceProvider {

  def dataSourceFor(
    url: String = "jdbc:mysql://localhost:3306/information_schema",
    user: String = "root",
    password: String = null): DataSource = {

    val cpds = new ComboPooledDataSource()
    cpds.setDriverClass("com.mysql.jdbc.Driver")
    cpds.setJdbcUrl(url)
    cpds.setUser(user)
    cpds.setPassword(password)
    cpds
  }
}
