package com.wixpress.embed.mysql.config

import de.flapdoodle.embed.process.config.io.ProcessOutput
import de.flapdoodle.embed.process.runtime.ICommandLinePostProcessor

/**
 * @author viliusl
 * @since 18/09/14
 */
class RuntimeConfigBuilder extends de.flapdoodle.embed.process.config.RuntimeConfigBuilder {

  def defaults(): RuntimeConfigBuilder = {
    processOutput().setDefault(ProcessOutput.getDefaultInstance("mysqld"))
    commandLinePostProcessor().setDefault(new ICommandLinePostProcessor.Noop)
    artifactStore().setDefault(new ArtifactStoreBuilder().defaults().build())
    return this
  }
}