package com.wixpress.embed.mysql

import java.io._
import java.util

import com.wixpress.embed.mysql.config.MysqldConfig
import com.wixpress.embed.mysql.runtime.Mysqld
import de.flapdoodle.embed.process.config.IRuntimeConfig
import de.flapdoodle.embed.process.distribution.Distribution
import de.flapdoodle.embed.process.extract.IExtractedFileSet
import de.flapdoodle.embed.process.runtime.{AbstractProcess, ProcessControl}

import scala.io.BufferedSource

/**
 * @author viliusl
 * @since 18/09/14
 */
class MysqldProcess(
  val distribution: Distribution,
  val config: MysqldConfig,
  val runtimeConfig: IRuntimeConfig,
  val executable: MysqldExecutable) extends AbstractProcess[MysqldConfig, MysqldExecutable, MysqldProcess](distribution, config, runtimeConfig, executable) {

  val processBuilder = ProcessControl.newProcessBuilder(
    runtimeConfig.getCommandLinePostProcessor().process(distribution,
      getCommandLine(distribution, config, executable.getFile())),
    getEnvironment(distribution, config, executable.getFile()), true);

  val process = ProcessControl.start(config.supportConfig(), processBuilder);


  override def onBeforeProcess(runtimeConfig: IRuntimeConfig): Unit = {
    super.onBeforeProcess(runtimeConfig)
    Support(executable.extractedFiles).initDatabase()
  }

  override def getCommandLine(distribution: Distribution, config: MysqldConfig, exe: IExtractedFileSet): util.List[String] = {
  Mysqld.getCommandLine(config, exe, null)
}

override def stopInternal(): Unit = {
  Support(executable.extractedFiles).stopInstance()
}

override def cleanupInternal(): Unit = {

}

}
