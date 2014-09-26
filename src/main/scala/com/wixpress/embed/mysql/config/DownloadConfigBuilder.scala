package com.wixpress.embed.mysql.config

import com.wixpress.embed.mysql.distribution.Version
import com.wixpress.embed.mysql.support.LoggingSupport
import de.flapdoodle.embed.process.config.store.DownloadConfigBuilder.{DownloadPrefix, UserAgent}
import de.flapdoodle.embed.process.config.store.{FileType, FileSet, IPackageResolver, IDownloadPath}
import de.flapdoodle.embed.process.distribution.{BitSize, Platform, ArchiveType, Distribution}
import de.flapdoodle.embed.process.extract.UUIDTempNaming
import de.flapdoodle.embed.process.io.directories.UserHome
import de.flapdoodle.embed.process.io.progress.StandardConsoleProgressListener

/**
 * @author viliusl
 * @since 18/09/14
 */
class DownloadConfigBuilder extends de.flapdoodle.embed.process.config.store.DownloadConfigBuilder with LoggingSupport {

  def defaults(): DownloadConfigBuilder = {
    fileNaming().setDefault(new UUIDTempNaming())
    downloadPath().setDefault(new DownloadPath())
    progressListener.setDefault(new StandardConsoleProgressListener())
    artifactStorePath.setDefault(new UserHome(".embedmysql"))
    downloadPrefix.setDefault(new DownloadPrefix("embedmysql-download"))
    userAgent.setDefault(new UserAgent("Mozilla/5.0 (compatible; Embedded MySql; +https://github.com/wix/wix-embedded-mysql)"))
    packageResolver().setDefault(new PackagePaths())
    this
  }

  class DownloadPath extends IDownloadPath {
    override def getPath(distribution: Distribution): String = "http://dev.mysql.com/get/Downloads/"
  }

  class PackagePaths extends IPackageResolver {

    override def getFileSet(distribution: Distribution): FileSet = {
      distribution.getPlatform match {
        case Platform.Windows => buildWindowsEntries()
        case _ => buildNixEntries()
      }
    }

    override def getArchiveType(distribution: Distribution): ArchiveType = {
      distribution.getPlatform match {
        case Platform.Windows => ArchiveType.ZIP
        case _ => ArchiveType.TGZ
      }
    }

    override def getPath(distribution: Distribution): String = {
      val ver = distribution.getVersion().asInstanceOf[Version]
      (distribution.getPlatform, distribution.getBitsize) match {
        case (Platform.OS_X, BitSize.B32) =>  s"mysql-${ver.name}-osx${ver.osVersion}-x86.tar.gz"
        case (Platform.OS_X, BitSize.B64) =>  s"mysql-${ver.name}-osx${ver.osVersion}-x86_64.tar.gz"
        case (Platform.Linux, BitSize.B32) => s"mysql-${ver.name}-linux-glibc2.5-i686.tar.gz"
        case (Platform.Linux, BitSize.B64) => s"mysql-${ver.name}-linux-glibc2.5-x86_64.tar.gz"
        case (Platform.Windows, BitSize.B32) => s"mysql-${ver.name}-win32.zip"
        case (Platform.Windows, BitSize.B64) => s"mysql-${ver.name}-winx64.zip"
        case (_, _) => ???
      }
    }

    private def buildWindowsEntries(): FileSet = {
      val builder = FileSet.builder()
        .addEntry(FileType.Executable,  "bin/mysqld.exe")
        .addEntry(FileType.Script,      "bin/mysqladmin.exe")
        .addEntry(FileType.Support,     "share/english/errmsg.sys")
        .addEntry(FileType.Support,     "data/test/db.opt")

      //TODO: patch up process library to support multi-match pattern.
      //then we could just have regex and mark it as multi-match and no file-counting
      //as this one is dodgy especially when considering multiple versions etc.
      (0 until  3).foreach(r => builder.addEntry(FileType.Support, "data/ib.*"))
      (0 until 79).foreach(r => builder.addEntry(FileType.Support, "data/mysql/.*"))
      (0 until 53).foreach(r => builder.addEntry(FileType.Support, "data/performance_schema/.*"))

      builder.build()
    }

    private def buildNixEntries(): FileSet = {
      FileSet.builder()
        .addEntry(FileType.Executable,  "bin/mysqld")
        .addEntry(FileType.Script,      "bin/mysqladmin")
        .addEntry(FileType.Script,      "bin/my_print_defaults")
        .addEntry(FileType.Script,      "scripts/mysql_install_db")
        .addEntry(FileType.Library,     "lib/plugin/innodb_engine.so")
        .addEntry(FileType.Support,     "share/english/errmsg.sys")
        .addEntry(FileType.Support,     "share/fill_help_tables.sql")
        .addEntry(FileType.Support,     "share/mysql_security_commands.sql")
        .addEntry(FileType.Support,     "share/mysql_system_tables.sql")
        .addEntry(FileType.Support,     "share/mysql_system_tables_data.sql")
        .addEntry(FileType.Support,     "support-files/my-default.cnf")
        .build()
    }
  }
}