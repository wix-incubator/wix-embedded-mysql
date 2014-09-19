package com.wixpress.embed.mysql.config

import de.flapdoodle.embed.process.config.store.{FileType, FileSet, IPackageResolver}
import de.flapdoodle.embed.process.distribution.{ArchiveType, Distribution}

/**
 * @author viliusl
 * @since 19/09/14
 */
class PackageResolver extends IPackageResolver {

  override def getFileSet(distribution: Distribution): FileSet = {
    //TODO: make proper platform aware
    FileSet.builder().addEntry(FileType.Executable, "mysqld").build()
  }

  override def getArchiveType(distribution: Distribution): ArchiveType = ArchiveType.TGZ

  override def getPath(distribution: Distribution): String = "MySQL-5.6/mysql-5.6.20-linux-glibc2.5-i686.tar.gz"

}
