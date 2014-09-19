package com.wixpress.embed.mysql

import com.wixpress.embed.mysql.config.MysqldConfigBuilder
import org.specs2.matcher.Scope
import org.specs2.mutable.SpecificationWithJUnit

/**
 * @author viliusl
 * @since 18/09/14
 */
class RunnerTest extends SpecificationWithJUnit {

  "Mysqld" should {

    "start without errors" in new Scope {
      val starter = MysqldStarter.defaultInstance
      val port = 12345

      val config = new MysqldConfigBuilder().build()
      val executable = starter.prepare(config)

      try {
        val mysqld = executable.start()
      } finally { executable.stop() }
    }

  }

}
