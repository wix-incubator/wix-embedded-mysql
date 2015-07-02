package com.wix.mysql

import com.wix.mysql.config.MysqldConfig
import com.wix.mysql.distribution.Version
import com.wix.mysql.distribution.Version._
import com.wix.mysql.exceptions.CommandFailedException

/**
 * @author viliusl
 * @since 25/05/15
 */
class MysqlTest extends IntegrationTest {

  "mysql should emit exception info with message from 'mysql' command output'" in {
    var mysqld: EmbeddedMysql = null
    try {
      mysqld = EmbeddedMysql.anEmbeddedMysql(Version.v5_6_latest).start()
      val mysql = new MysqlClient(mysqld.getConfig, mysqld.executable)

      mysql.executeCommands("sele qwe from zz;") must throwA[CommandFailedException].like {
        case e: CommandFailedException => e.getMessage must contain(
          "output 'ERROR 1064 (42000) at line 1: You have an error in your SQL syntax;")
      }
    } finally mysqld.stop
  }
}
