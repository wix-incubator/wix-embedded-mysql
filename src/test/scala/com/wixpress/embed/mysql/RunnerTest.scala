package com.wixpress.embed.mysql

import javax.sql.DataSource

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
  sequential

  trait ctx extends Scope {
    val starter = MysqldStarter.defaultInstance

    def verifyConnection(dataSource: DataSource): Unit = {
      val template = new JdbcTemplate(DataSourceProvider.dataSourceFor())
      template.queryForObject("select 1 from dual;", classOf[java.lang.Long]) must_== 1
    }
  }

  "Mysqld" should {

    "start with all defaults" in new ctx {
      val config = new MysqldConfig(Version.v5_6_21)
      val executable = starter.prepare(config)
      try {
        val mysqld = executable.start()
        verifyConnection(DataSourceProvider.dataSourceFor())
      } catch {
        case e : Exception => println(e.getMessage); e.printStackTrace; throw e;
      } finally { executable.stop() }
    }

    "custom port" in new ctx {
      val config = new MysqldConfig(Version.v5_6_21, 3301)
      val executable = starter.prepare(config)

      try {
        val mysqld = executable.start()
        verifyConnection(DataSourceProvider.dataSourceFor(url = s"jdbc:mysql://localhost:3301/information_schema"))
      } catch {
        case e : Exception => println(e.getMessage); e.printStackTrace; throw e;
      } finally { executable.stop() }
    }
  }
}
