package com.wixpress.embed.mysql.config

import de.flapdoodle.embed.process.config.{ExecutableProcessConfig, ISupportConfig}
import de.flapdoodle.embed.process.distribution.IVersion

/**
 * @author viliusl
 * @since 18/09/14
 */
class MysqldConfig(
  version: IVersion,
  val port: Int = 3306) extends ExecutableProcessConfig(version, new MysqldSupportConfig()) {
}

class MysqldSupportConfig extends ISupportConfig {
  override def messageOnException(context: Class[_], exception: Exception): String = ""
  override def getName: String = "mysqld"
  override def getSupportUrl: String = "https://github.com/wix/wix-embedded-mysql/issues"
}

