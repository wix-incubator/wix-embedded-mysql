package com.wix.mysql

import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.config.MysqldConfig
import com.wix.mysql.distribution.Version
import com.wix.mysql.support.IntegrationTest
import com.wix.mysql.support.IntegrationTest.testConfigBuilder
import org.specs2.matcher.Scope
import org.specs2.specification.core.Fragment

class SupportedVersionsTest extends IntegrationTest {

  trait Context extends Scope {
    val log: Iterable[String] = aLogFor("root")
  }

  Fragment.foreach(versionsToTest()) { version =>

    s"$version should work on ${System.getProperty("os.name")}" in new Context {
      val config: MysqldConfig = testConfigBuilder.build

      val mysqld: EmbeddedMysql = start(anEmbeddedMysql(config).addSchema("aschema"))

      mysqld must beAvailableOn(config, "aschema")

      log must not(contain("Something bad happened."))
    }
  }

  def versionsToTest(): Seq[Version] = {
    val osSupportedVersions = Version.values filter (_.supportsCurrentPlatform)
    val distinctMajorVersions = osSupportedVersions.foldLeft(Seq[Version]())((collected, v) => {
      collected.find(alreadyCollected => alreadyCollected.getMajorVersion == v.getMajorVersion) match {
        case Some(_) => collected
        case _ => collected :+ v
      }
    })

    distinctMajorVersions
  }
}
