package com.wix.mysql

import com.wix.mysql.config.MysqldConfig
import com.wix.mysql.distribution.Version._
import com.wix.mysql.exceptions.CommandFailedException

/**
 * @author viliusl
 * @since 25/05/15
 */
class MysqlTest extends IntegrationTest {

  val config = MysqldConfig.Builder(v5_6_21).build()

  "mysql should emit exception info with message from 'mysql' command output'" in {
    val executable: MysqldExecutable = givenMySqlWithConfig(config)
    try {
      val process = executable.start()
      val mysql = new MysqlClient(config, executable)

      mysql.executeCommands("sele qwe from zz;") must throwA[CommandFailedException].like {
        case e: CommandFailedException => e.getMessage must contain(
          "output 'ERROR 1064 (42000) at line 1: You have an error in your SQL syntax;")
      }
    } finally executable.stop
  }
}
