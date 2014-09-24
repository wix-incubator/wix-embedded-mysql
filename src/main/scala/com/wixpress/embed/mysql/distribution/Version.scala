package com.wixpress.embed.mysql.distribution

import de.flapdoodle.embed.process.distribution.IVersion

/**
 * @author viliusl
 * @since 24/09/14
 */
sealed trait Version extends IVersion {
  val name: String;

  //TODO: need to figure out a better way to check/verify minimal version.
  //ex osx version is 10.6, but mysql is build for osx 10.8
  //maybe it should be in {@link PackagePaths}
  val osVersion: String
  override def asInDownloadPath(): String = name

}
object Version {
  case object v5_6_21 extends Version { override val name = "5.6.21"; override val osVersion = "10.8"}
  case object v5_5_39 extends Version { override val name = "5.5.39"; override val osVersion = "10.6"}
}

