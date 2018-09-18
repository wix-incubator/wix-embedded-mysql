package com.wix.mysql

import java.util.{Calendar, Date, SimpleTimeZone}

import com.wix.mysql.utils.Utils
import org.specs2.mutable.SpecWithJUnit

class UtilsTest extends SpecWithJUnit {
  "Utils" should {
    "take daylight savings into account" in {
      val cal = Calendar.getInstance()
      val millisInHour = 60 * 60 * 1000
      // this fake tz should always be in daylight time:
      val tz = new SimpleTimeZone(millisInHour * -5, "EDT", Calendar.JANUARY, 1, 0, 0, Calendar.DECEMBER, 31, 0, millisInHour * 24 - 1, millisInHour)
      val offset = Utils.asHHmmOffset(tz)
      tz.observesDaylightTime() mustEqual true
      tz.inDaylightTime(new Date()) mustEqual true
      offset mustEqual "-04:00"
    }
  }
}
