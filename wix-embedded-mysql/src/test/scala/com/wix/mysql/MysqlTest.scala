package com.wix.mysql

import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.config.Charset
import com.wix.mysql.exceptions.CommandFailedException
import com.wix.mysql.support.IntegrationTest
import com.wix.mysql.support.IntegrationTest.targetTestVersion

class MysqlTest extends IntegrationTest {

  "mysql should emit exception info with message from 'mysql' command output'" in {
    val mysqld = start(anEmbeddedMysql(targetTestVersion))
    val mysql = new MysqlClient(mysqld.getConfig, mysqld.executable, "information_schema", Charset.UTF8MB4)

    mysql.executeCommands("sele qwe from zz;") must throwA[CommandFailedException].like {
      case e: CommandFailedException => e.getMessage must contain(
        "ERROR 1064 (42000) at line 1: You have an error in your SQL syntax;")
    }
  }
}
