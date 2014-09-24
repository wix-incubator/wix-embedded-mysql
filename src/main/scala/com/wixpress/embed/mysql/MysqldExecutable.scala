package com.wixpress.embed.mysql

import com.wixpress.embed.mysql.config.MysqldConfig
import de.flapdoodle.embed.process.config.IRuntimeConfig
import de.flapdoodle.embed.process.distribution.Distribution
import de.flapdoodle.embed.process.extract.IExtractedFileSet
import de.flapdoodle.embed.process.runtime.Executable

/**
 * @author viliusl
 * @since 18/09/14
 */
class MysqldExecutable(
  distribution: Distribution,
  config: MysqldConfig,
  runtimeConfig: IRuntimeConfig,
  val extractedFiles: IExtractedFileSet)
  extends Executable[MysqldConfig, MysqldProcess](distribution, config, runtimeConfig, extractedFiles) {

  override def start(distribution: Distribution, config: MysqldConfig, runtimeConfig: IRuntimeConfig): MysqldProcess =
    new MysqldProcess(distribution, config, runtimeConfig, this)
}
