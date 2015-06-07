package com.wix.mysql

import com.wix.mysql.ClassPathScriptResolver.file
import com.wix.mysql.config.{SchemaConfig, MysqldConfig}
import com.wix.mysql.distribution.Version
import org.specs2.matcher.Scope
import org.specs2.specification.core.Fragment

/**
 * @author viliusl
 * @since 27/03/15
 */
class SupportedVersionsTest extends IntegrationTest {

  trait Context extends Scope {
    val schema = SchemaConfig.Builder("aschema").withScripts(file("db/001_init.sql")).build()
    val log = aLogFor("root")
  }

  Fragment.foreach( Version.values filter(_.supportsCurrentPlatform) ) { version =>
    s"${version} should work on ${System.getProperty("os.name")}" in new Context {
      startAndVerifyDatabase(MysqldConfig.Builder(version).build, schema)
      log must not(contain("Something bad happened."))
    }
  }
}
