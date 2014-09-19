package com.wixpress.embed.mysql.config

import de.flapdoodle.embed.process.config.ISupportConfig
import de.flapdoodle.embed.process.distribution.IVersion

/**
 * @author viliusl
 * @since 18/09/14
 */
class MysqldConfig extends de.flapdoodle.embed.process.config.IExecutableProcessConfig {
  override def version(): IVersion = new IVersion {
    override def asInDownloadPath(): String = "12"
  }

  override def supportConfig(): ISupportConfig = new ISupportConfig {
    override def messageOnException(context: Class[_], exception: Exception): String = "woops"

    override def getName: String = "mysqld"

    override def getSupportUrl: String = "someUrl"
  }
}
