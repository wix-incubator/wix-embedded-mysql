package com.wix.mysql

import com.wix.mysql.config.Charset.{LATIN1, defaults}
import com.wix.mysql.config.MysqldConfig.aMysqldConfig
import com.wix.mysql.distribution.Version._
import org.specs2.mutable.SpecWithJUnit

/**
 * @author viliusl
 * @since 28/05/15
 */
class MysqldConfigTest extends SpecWithJUnit {

  "MysqldConfig" should {

    "build with defaults" in {
      val mysqldConfig = aMysqldConfig(v5_6_latest).build()

      mysqldConfig.getPort mustEqual 3310
      mysqldConfig.getVersion mustEqual v5_6_latest
      mysqldConfig.getCharset mustEqual defaults()
      mysqldConfig.getUsername mustEqual "auser"
      mysqldConfig.getPassword mustEqual "sa"
    }

    "accept custom port, user, charset" in {
      val mysqldConfig = aMysqldConfig(v5_6_latest)
        .withPort(1111)
        .withCharset(LATIN1)
        .withUser("otheruser", "otherpassword")
        .build()

      mysqldConfig.getPort mustEqual 1111
      mysqldConfig.getCharset mustEqual LATIN1
      mysqldConfig.getUsername mustEqual "otheruser"
      mysqldConfig.getPassword mustEqual "otherpassword"
    }
  }
}