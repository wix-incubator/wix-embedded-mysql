package com.wix.mysql

import com.wix.mysql.config.MysqldConfigBuilder
import com.wix.mysql.distribution.Version
import org.specs2.matcher.Scope

/**
 * @author viliusl
 * @since 27/03/15
 */
class SupportedVersionsTest extends IntegrationTest {

  trait Context extends Scope {
    val log = aLogFor("root")
  }

  Version.values filter { _.supportsCurrentPlatform() } foreach { version =>
    s"${version} should work on ${System.getProperty("os.name")}" in new Context {
      startAndVerifyDatabase( new MysqldConfigBuilder(version).build )
      log must not(contain("Something bad happened."))
    }
  }
}
