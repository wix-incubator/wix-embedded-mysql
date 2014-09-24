package com.wixpress.embed.mysql

import com.wixpress.embed.mysql.config.MysqldConfig
import com.wixpress.embed.mysql.config.RuntimeConfigBuilder
import de.flapdoodle.embed.process.config.{IRuntimeConfig}
import de.flapdoodle.embed.process.distribution.Distribution
import de.flapdoodle.embed.process.extract.IExtractedFileSet
import de.flapdoodle.embed.process.runtime.Starter

/**
 * @author viliusl
 * @since 18/09/14
 */
class MysqldStarter(runtimeConfig: IRuntimeConfig) extends Starter[MysqldConfig, MysqldExecutable, MysqldProcess](runtimeConfig) {

  override def newExecutable(
      mysqldConfig: MysqldConfig,
      distribution: Distribution,
      runtimeConfig: IRuntimeConfig,
      extractedFiles: IExtractedFileSet): MysqldExecutable = {
    new MysqldExecutable(distribution, mysqldConfig, runtimeConfig, extractedFiles)
  }
}

object MysqldStarter {
  def instance(config: IRuntimeConfig) = new MysqldStarter(config)
  def defaultInstance = new MysqldStarter(new RuntimeConfigBuilder().defaults().build())
}
