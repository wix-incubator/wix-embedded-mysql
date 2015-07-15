package com.wix.mysql

import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.config.MysqldConfig.aMysqldConfig
import com.wix.mysql.distribution.Version
import org.specs2.matcher.Scope
import org.specs2.specification.core.Fragment

/**
 * @author viliusl
 * @since 27/03/15
 */
class SupportedVersionsTest extends IntegrationTest {

  trait Context extends Scope {
    val log = aLogFor("root")
  }

  Fragment.foreach( Version.values filter(_.supportsCurrentPlatform) ) { version =>

    s"${version} should work on ${System.getProperty("os.name")}" in new Context {
      val config = aMysqldConfig(version).build

      mysqld = anEmbeddedMysql(config)
        .addSchema("aschema")
        .start

      validateConnection(config, "aschema")
      log must not(contain("Something bad happened."))
    }
  }
}