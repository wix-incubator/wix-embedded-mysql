package com.wix.mysql

import com.wix.mysql.config.{Charset, MysqldConfig}
import com.wix.mysql.distribution.Version
import com.wix.mysql.distribution.Version._
import org.specs2.mutable.SpecWithJUnit

/**
 * @author viliusl
 * @since 28/05/15
 */
class MysqldConfigTest extends SpecWithJUnit {

  "MysqldConfig" should {

    "build with defaults" in {
      val mysqldConfig = MysqldConfig.Builder(v5_6_latest).build()

      mysqldConfig.getPort mustEqual 3310
      mysqldConfig.getVersion mustEqual Version.v5_6_latest
      mysqldConfig.getCharset mustEqual Charset.defaults()
      mysqldConfig.getUsername mustEqual "auser"
      mysqldConfig.getPassword mustEqual "sa"
    }

    "accept custom port, user, charset" in {
      val mysqldConfig = MysqldConfig.Builder(v5_6_latest)
        .withPort(1111)
        .withCharset(Charset.LATIN1)
        .withUser("otheruser", "otherpassword")
        .build()

      mysqldConfig.getPort mustEqual 1111
      mysqldConfig.getCharset mustEqual Charset.LATIN1
      mysqldConfig.getUsername mustEqual "otheruser"
      mysqldConfig.getPassword mustEqual "otherpassword"
    }
  }
}