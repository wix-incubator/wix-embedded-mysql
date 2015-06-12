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

    "provide defaults" in {
      val mysqldConfig = MysqldConfig.defaults(v5_6_latest)

      mysqldConfig.getPort() mustEqual 3310
      mysqldConfig.getVersion() mustEqual Version.v5_6_latest
      mysqldConfig.getCharset() mustEqual Charset.defaults()
    }

    "build with defaults" in {
      val mysqldConfig = MysqldConfig.Builder(v5_6_latest).build()

      mysqldConfig.getPort() mustEqual 3310
      mysqldConfig.getVersion() mustEqual Version.v5_6_latest
      mysqldConfig.getCharset() mustEqual Charset.defaults()
    }

    "accept custom port" in {
      val mysqldConfig = MysqldConfig.Builder(v5_6_latest)
        .withPort(1111)
        .build()

      mysqldConfig.getPort() mustEqual 1111
    }

    "accept custom charset" in {
      val mysqldConfig = MysqldConfig.Builder(v5_6_latest)
        .withCharset(Charset.LATIN1)
        .build()

      mysqldConfig.getCharset() mustEqual Charset.LATIN1
    }
  }
}