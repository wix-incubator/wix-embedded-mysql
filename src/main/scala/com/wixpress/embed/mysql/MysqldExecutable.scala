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
  p1: Distribution,
  p2: MysqldConfig,
  p3: IRuntimeConfig,
  p4: IExtractedFileSet) extends Executable[MysqldConfig, MysqldProcess](p1, p2, p3, p4) {


  override def start(p1: Distribution, p2: MysqldConfig, p3: IRuntimeConfig): MysqldProcess =
    new MysqldProcess()
}
