package com.wixpress.embed.mysql.config

import de.flapdoodle.embed.process.config.io.ProcessOutput
import de.flapdoodle.embed.process.runtime.ICommandLinePostProcessor

/**
 * @author viliusl
 * @since 18/09/14
 */
class RuntimeConfigBuilder extends de.flapdoodle.embed.process.config.RuntimeConfigBuilder {

  def default(): RuntimeConfigBuilder = {
    processOutput().setDefault(ProcessOutput.getDefaultInstance("mysqld"))
    commandLinePostProcessor().setDefault(new ICommandLinePostProcessor.Noop)
    artifactStore().setDefault(new ArtifactStoreBuilder().default().build())
    return this
  }
}