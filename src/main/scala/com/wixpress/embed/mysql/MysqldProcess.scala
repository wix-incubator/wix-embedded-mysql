package com.wixpress.embed.mysql

import java.io._
import java.util

import com.wixpress.embed.mysql.config.MysqldConfig
import com.wixpress.embed.mysql.runtime.Mysqld
import com.wixpress.embed.mysql.support.LoggingSupport
import de.flapdoodle.embed.process.config.IRuntimeConfig
import de.flapdoodle.embed.process.distribution.Distribution
import de.flapdoodle.embed.process.extract.IExtractedFileSet
import de.flapdoodle.embed.process.io.{LogWatchStreamProcessor, Processors, StreamToLineProcessor}
import de.flapdoodle.embed.process.runtime.{AbstractProcess, ProcessControl}

import scala.collection.JavaConversions._
import scala.io.BufferedSource
import scala.util.{Success, Try}

/**
 * @author viliusl
 * @since 18/09/14
 */
class MysqldProcess(
    distribution: Distribution,
    config: MysqldConfig,
    runtimeConfig: IRuntimeConfig,
    val executable: MysqldExecutable)
  extends AbstractProcess[MysqldConfig, MysqldExecutable, MysqldProcess](distribution, config, runtimeConfig, executable) with LoggingSupport {

  var stopped = false
  val postProcessor = runtimeConfig.getCommandLinePostProcessor().process(
    distribution,
    getCommandLine(distribution, config, executable.getFile()))

  val processBuilder = ProcessControl.newProcessBuilder(postProcessor, true);

  val process = ProcessControl.start(config.supportConfig(), processBuilder);

  override def onBeforeProcess(runtimeConfig: IRuntimeConfig): Unit = {
    super.onBeforeProcess(runtimeConfig)
    initDatabase()
  }

  override def getCommandLine(distribution: Distribution, config: MysqldConfig, exe: IExtractedFileSet): util.List[String] = {
    Mysqld.getCommandLine(config, exe, pidFile(exe.executable()))
  }

  override def stopInternal(): Unit = {
    this synchronized {
      if (!stopped) {
        stopped = true

        log.info("try to stop mysqld")
        stopUsingMysqldadmin().orElse {
          log.warn("could not stop mysqld via mysqladmin, try next")
          if (!sendKillToProcess) {
            log.warn("could not stop mysqld, try next")
            if (!sendTermToProcess) {
              log.warn("could not stop mysqld, try next")
              if (!tryKillToProcess) {
                log.warn("could not stop mysqld the second time, try one last thing")
              }
            }
          }
          stopProcess
          Success()
        }
      }
    }
  }

  override def cleanupInternal(): Unit = {
  }

  override def onAfterProcessStart(process: ProcessControl, runtimeConfig: IRuntimeConfig): Unit = {
    val logWatch: LogWatchStreamProcessor = new LogWatchStreamProcessor(
      "bin/mysqld: ready for connections",
      Set[String]("[ERROR]"),
      StreamToLineProcessor.wrap(runtimeConfig.getProcessOutput.getOutput))

    Processors.connect(process.getReader, logWatch)
    Processors.connect(process.getError, logWatch)

    logWatch.waitForResult(config.timeout)

    if (!logWatch.isInitWithSuccess)
      throw new RuntimeException("mysql start failed with error: " + logWatch.getFailureFound)
    else
      setProcessId(AbstractProcess.getPidFromFile(pidFile))
  }

  override def pidFile(executableFile: File): File = {
    new File(s"${executableFile.getAbsolutePath}.pid")
  }

  private def stopUsingMysqldadmin(): Try[Unit] = Try {
    val p = Runtime.getRuntime.exec(Array[String](
      "bin/mysqladmin",
      "-uroot",//user, should be different if auth method is different, password is needed as well
      "-hlocalhost",
      "--protocol=tcp",
      //"--count=3", "--sleep=3",//try 3 times with 3 seconds interval
      s"--port=${config.port}",
      "shutdown"),
      Array[String](),
      executable.extractedFiles.generatedBaseDir())

    val retCode = p.waitFor()

    new BufferedSource(p.getInputStream).getLines().foreach(l => log.trace(s"bin/mysqladmin out: $l"))
    new BufferedSource(p.getErrorStream).getLines().foreach(l => log.trace(s"bin/mysqladmin err: $l"))

    if (retCode != 0) throw new RuntimeException(s"bin/mysqladmin stop command exited with error code: $retCode")
  }

  def initDatabase(): Unit = {
    val p = Runtime.getRuntime.exec(Array[String](
      "scripts/mysql_install_db",
      "--force", // do not lookup dns - no need for resolveip command to be present
      "--no-defaults"), // do not read defaults file.
      Array[String](),
      executable.extractedFiles.generatedBaseDir())

    val retCode = p.waitFor()

    new BufferedSource(p.getInputStream).getLines().foreach(line => log.trace(s"scripts/mysql_install_db out: $line"))
    new BufferedSource(p.getErrorStream).getLines().foreach(line => log.trace(s"scripts/mysql_install_db err: $line"))

    if (retCode != 0) throw new RuntimeException(s"'scripts/mysql_install_db' command exited with error code: $retCode")
  }
}
