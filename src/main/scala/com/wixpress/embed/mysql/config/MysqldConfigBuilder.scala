package com.wixpress.embed.mysql.config

/**
 * @author viliusl
 * @since 18/09/14
 */
class MysqldConfigBuilder extends de.flapdoodle.embed.process.builder.AbstractBuilder[MysqldConfig] {

  override def build(): MysqldConfig = new MysqldConfig()

}
