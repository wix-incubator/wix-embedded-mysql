package com.wix.mysql.distribution

import java.lang.System._

import com.wix.mysql.distribution.Version._
import com.wix.mysql.exceptions.UnsupportedPlatformException
import de.flapdoodle.embed.process.distribution.Platform
import de.flapdoodle.embed.process.distribution.Platform._
import org.specs2.execute.{AsResult, Result}
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.AroundEach

class VersionTest extends SpecWithJUnit with AroundEach {
  sequential

  "platform detection should detect" >> {
    "OS X" in {
      givenPlatformSetTo(OS_X)
      v5_7_15.asInDownloadPath mustEqual "MySQL-5.7/mysql-5.7.15-osx10.11"
    }

    "Windows" in {
      givenPlatformSetTo(Windows)
      v5_7_15.asInDownloadPath mustEqual "MySQL-5.7/mysql-5.7.15"
    }

    "Linux" in {
      givenPlatformSetTo(Linux)
      v5_7_15.asInDownloadPath mustEqual "MySQL-5.7/mysql-5.7.15-linux-glibc2.5"
    }
  }

  "verify that" >> {
    "windows for 5.5.X is not supported" in {
      givenPlatformSetTo(Windows)
      v5_5_latest.asInDownloadPath must throwA[UnsupportedPlatformException]
    }

    "solaris is not supported" in {
      givenPlatformSetTo(Solaris)
      v5_5_latest.asInDownloadPath must throwA[UnsupportedPlatformException]
    }

    "freebsd is not supported" in {
      givenPlatformSetTo(FreeBSD)
      v5_5_latest.asInDownloadPath must throwA[UnsupportedPlatformException]
    }

    "x dev api is supported for version 5.7.12 and higher" in {
      Version.values.filter( _.getMajorVersion == "5.5" )
                    .foreach( _.supportDevXApi must beFalse )
      Version.values.filter( _.getMajorVersion == "5.6" )
                    .foreach( _.supportDevXApi must beFalse )
      Version.values.filter( _.getMajorVersion == "5.7" )
                    .filter( _.getMinorVersion < 12 )
                    .foreach( _.supportDevXApi must beFalse )
      Version.values.filter( _.getMajorVersion == "5.7" )
                    .filter( _.getMinorVersion >= 12 )
                    .foreach( _.supportDevXApi must beTrue )
      Version.values.filter( _.getMajorVersion == "5.8" )
                    .foreach( _.supportDevXApi must beTrue )
      success
    }
  }

  def givenPlatformSetTo(platform: Platform) = platform match {
    case Windows => setProperty("os.name", "Windows")
    case OS_X => setProperty("os.name", "Mac OS X")
    case Linux => setProperty("os.name", "Linux")
    case Solaris => setProperty("os.name", "SunOS")
    case FreeBSD => setProperty("os.name", "FreeBSD")
    case _ => throw new UnsupportedPlatformException("Unrecognized platform, currently not supported")
  }

  def around[R: AsResult](r: => R): Result = {
    val current = getProperty("os.name")
    try AsResult(r)
    finally setProperty("os.name", current)
  }
}
