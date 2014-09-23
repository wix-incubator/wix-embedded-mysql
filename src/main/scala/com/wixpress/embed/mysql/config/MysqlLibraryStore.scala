package com.wixpress.embed.mysql.config

import java.util

import de.flapdoodle.embed.process.config.store.ILibraryStore
import de.flapdoodle.embed.process.distribution.Distribution

/**
 * @author viliusl
 * @since 19/09/14
 */
class MysqlLibraryStore extends ILibraryStore {
  override def getLibrary(distribution: Distribution): util.List[String] = {
    util.Arrays.asList("libmysqlclient.18.dylib", "libmysqlclient_r.18.dylib", "libmysqlclient_r.dylib")
  }
}
