package com.wix.mysql

import java.util.TimeZone

import com.wix.mysql.config.Charset.{LATIN1, defaults}
import com.wix.mysql.config.MysqldConfig.aMysqldConfig
import com.wix.mysql.distribution.Version._
import org.specs2.mutable.SpecWithJUnit

class MysqldConfigTest extends SpecWithJUnit {

  "MysqldConfig" should {

    "build with defaults" in {
      val mysqldConfig = aMysqldConfig(v5_6_latest).build()

      mysqldConfig.getPort mustEqual 3310
      mysqldConfig.getVersion mustEqual v5_6_latest
      mysqldConfig.getCharset mustEqual defaults()
      mysqldConfig.getUsername mustEqual "auser"
      mysqldConfig.getPassword mustEqual "sa"
      mysqldConfig.getTimeZone mustEqual TimeZone.getTimeZone("UTC")
      mysqldConfig.getTimeout mustEqual 60000
    }

    "accept custom port, user, charset, timezone" in {
      val mysqldConfig = aMysqldConfig(v5_6_latest)
        .withPort(1111)
        .withCharset(LATIN1)
        .withUser("otheruser", "otherpassword")
        .withTimeZone("Europe/Vilnius")
        .withTimeout(10000)
        .build()

      mysqldConfig.getPort mustEqual 1111
      mysqldConfig.getCharset mustEqual LATIN1
      mysqldConfig.getUsername mustEqual "otheruser"
      mysqldConfig.getPassword mustEqual "otherpassword"
      mysqldConfig.getTimeZone mustEqual TimeZone.getTimeZone("Europe/Vilnius")
      mysqldConfig.getTimeout mustEqual 10000
    }

    "fail if building with user 'root'" in {
      aMysqldConfig(v5_6_latest)
        .withUser("root", "doesnotmatter")
        .build() must throwA[IllegalArgumentException](message = "Usage of username 'root' is forbidden")

    }

  }
}