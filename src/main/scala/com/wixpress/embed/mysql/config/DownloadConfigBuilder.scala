package com.wixpress.embed.mysql.config

import de.flapdoodle.embed.process.config.store.DownloadConfigBuilder.{UserAgent, DownloadPrefix}
import de.flapdoodle.embed.process.config.store.IDownloadPath
import de.flapdoodle.embed.process.distribution.Distribution
import de.flapdoodle.embed.process.extract.UUIDTempNaming
import de.flapdoodle.embed.process.io.directories.UserHome
import de.flapdoodle.embed.process.io.progress.StandardConsoleProgressListener

/**
 * @author viliusl
 * @since 18/09/14
 */
class DownloadConfigBuilder extends de.flapdoodle.embed.process.config.store.DownloadConfigBuilder {

  def default(): DownloadConfigBuilder = {
    fileNaming().setDefault(new UUIDTempNaming)
    downloadPath().setDefault(new DownloadPath)
    progressListener.setDefault(new StandardConsoleProgressListener)
    artifactStorePath.setDefault(new UserHome(".embedmysql"))
    downloadPrefix.setDefault(new DownloadPrefix("embedmysql-download"))
    userAgent.setDefault(new UserAgent("Mozilla/5.0 (compatible; Embedded MySql; +https://github.com/zzz)"))
    packageResolver().setDefault(new PackageResolver())
    this
  }
}

class DownloadPath extends IDownloadPath {
  override def getPath(p1: Distribution): String = "http://dev.mysql.com/get/Downloads/"
}

