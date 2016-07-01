package com.wix.mysql

import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.config.MysqldConfig.aMysqldConfig
import com.wix.mysql.distribution.Version
import com.wix.mysql.support.IntegrationTest
import org.specs2.matcher.Scope
import org.specs2.specification.core.Fragment

class SupportedVersionsTest extends IntegrationTest {

  trait Context extends Scope {
    val log = aLogFor("root")
  }

  Fragment.foreach(Version.values filter (_.supportsCurrentPlatform)) { version =>

    s"$version should work on ${System.getProperty("os.name")}" in new Context {
      val config = aMysqldConfig(version).build

      val mysqld = start(anEmbeddedMysql(config).addSchema("aschema"))

      mysqld must beAvailableOn(config, "aschema")

      log must not(contain("Something bad happened."))
    }
  }
}
