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
class MysqldStarter(p1: IRuntimeConfig)
  extends Starter[MysqldConfig, MysqldExecutable, MysqldProcess](p1) {

  override def newExecutable(
      p1: MysqldConfig,
      p2: Distribution,
      p3: IRuntimeConfig,
      p4: IExtractedFileSet): MysqldExecutable = {
    println(p4)
    new MysqldExecutable(p2, p1, p3, p4)
  }
}

object MysqldStarter {

  def instance(config: IRuntimeConfig) = new MysqldStarter(config)
  def defaultInstance = new MysqldStarter(new RuntimeConfigBuilder().defaults().build())
}
