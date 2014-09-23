package com.wixpress.embed.mysql.config

import java.io.File
import java.nio.file.Files
import java.util.UUID

import de.flapdoodle.embed.process.extract.{ITempNaming, UUIDTempNaming}
import de.flapdoodle.embed.process.io.directories.{IDirectory, PlatformTempDir, PropertyOrPlatformTempDir}

/**
 * @author viliusl
 * @since 19/09/14
 */
class ArtifactStoreBuilder extends de.flapdoodle.embed.process.store.ArtifactStoreBuilder {

  def default(): ArtifactStoreBuilder = {
    tempDir().setDefault(ArtifactStoreBuilder.tempDir)
    executableNaming().setDefault(ArtifactStoreBuilder.notTempNaming())
    download().setDefault(new DownloadConfigBuilder().default().build())
    libraries().setDefault(new MysqlLibraryStore())
    this
  }

}

object ArtifactStoreBuilder {

  def tempDir = new IDirectory {
    override def isGenerated: Boolean = true

    override def asFile(): File = Files.createTempDirectory("mysql").toFile
  }

  def notTempNaming() = new ITempNaming {
    //I don't need temp naming - temp dir is enough for me
    override def nameFor(prefix: String, postfix: String): String = postfix
  }
}
