package com.wixpress.embed.mysql.config

import java.io.File
import java.nio.file.Files

import de.flapdoodle.embed.process.extract.ITempNaming
import de.flapdoodle.embed.process.io.directories.IDirectory

/**
 * @author viliusl
 * @since 19/09/14
 */
class ArtifactStoreBuilder extends de.flapdoodle.embed.process.store.ArtifactStoreBuilder {

  def defaults(): ArtifactStoreBuilder = {
    tempDir().setDefault(new TempDirectory())
    executableNaming().setDefault(new OriginalNaming())
    download().setDefault(new DownloadConfigBuilder().defaults().build())
    this
  }

  //put everything in temp folder
  class TempDirectory extends IDirectory {
    override def asFile(): File = Files.createTempDirectory("mysql").toFile
    override def isGenerated: Boolean = true
  }

  //do not rename artifacts
  class OriginalNaming extends ITempNaming {
    override def nameFor(prefix: String, postfix: String): String = postfix
  }

}