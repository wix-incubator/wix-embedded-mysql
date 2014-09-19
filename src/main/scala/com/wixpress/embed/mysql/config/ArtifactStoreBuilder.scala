package com.wixpress.embed.mysql.config

import de.flapdoodle.embed.process.extract.UUIDTempNaming
import de.flapdoodle.embed.process.io.directories.PropertyOrPlatformTempDir

/**
 * @author viliusl
 * @since 19/09/14
 */
class ArtifactStoreBuilder extends de.flapdoodle.embed.process.store.ArtifactStoreBuilder {

  def default(): ArtifactStoreBuilder = {
    tempDir().setDefault(new PropertyOrPlatformTempDir)
    executableNaming().setDefault(new UUIDTempNaming)
    download().setDefault(new DownloadConfigBuilder().default().build())

    this
  }
}
