package com.wixpress.embed.mysql

import com.wixpress.embed.mysql.config.MysqldConfig
import com.wixpress.embed.mysql.distribution.Version
import org.specs2.matcher.Scope
import org.specs2.mutable.SpecificationWithJUnit
import org.springframework.jdbc.core.JdbcTemplate

/**
 * @author viliusl
 * @since 18/09/14
 */
class RunnerTest extends SpecificationWithJUnit {

  "Mysqld" should {

    "start without errors" in new Scope {
      val starter = MysqldStarter.defaultInstance
      val port = 12345

      val config = new MysqldConfig(Version.v5_6_21)
      val executable = starter.prepare(config)
      println("prepared")
      try {
        val mysqld = executable.start()

        val template = new JdbcTemplate(DataSourceProvider.dataSourceFor())
        template.queryForObject("select 1 from dual;", classOf[java.lang.Long]) must_== 1

        println("started")
      } catch {
        case e : Exception => println(e.getMessage); e.printStackTrace;

      } finally { executable.stop() }
    }

  }

}
