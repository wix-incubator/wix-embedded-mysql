package com.wix.mysql

import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.distribution.Version.v5_6_latest
import com.wix.mysql.exceptions.CommandFailedException
import com.wix.mysql.support.IntegrationTest

class MysqlTest extends IntegrationTest {

  "mysql should emit exception info with message from 'mysql' command output'" in {
    val mysqld = start(anEmbeddedMysql(v5_6_latest))
    val mysql = new MysqlClient(mysqld.getConfig, mysqld.executable, "information_schema")

    mysql.executeCommands("sele qwe from zz;") must throwA[CommandFailedException].like {
      case e: CommandFailedException => e.getMessage must contain(
        "output 'ERROR 1064 (42000) at line 1: You have an error in your SQL syntax;")
    }
  }
}
