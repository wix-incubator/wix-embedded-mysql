package com.wix.mysql.distribution

import java.lang.System._

import com.wix.mysql.distribution.Version._
import com.wix.mysql.exceptions.UnsupportedPlatformException
import de.flapdoodle.embed.process.distribution.Platform
import de.flapdoodle.embed.process.distribution.Platform._
import org.specs2.execute.{AsResult, Result}
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.AroundEach

/**
  * @author viliusl
  * @since 27/03/15
  */
class VersionTest extends SpecWithJUnit with AroundEach {
  sequential

  "platform detection should detect" >> {
    "OS X" in {
      givenPlatformSetTo(OS_X)
      v5_6_21.asInDownloadPath mustEqual "MySQL-5.6/mysql-5.6.21-osx10.9"
    }

    "Windows" in {
      givenPlatformSetTo(Windows)
      v5_6_21.asInDownloadPath mustEqual "MySQL-5.6/mysql-5.6.21"
    }

    "Linux" in {
      givenPlatformSetTo(Linux)
      v5_5_40.asInDownloadPath mustEqual "MySQL-5.5/mysql-5.5.40-linux2.6"
    }
  }

  "verify that" >> {
    "windows for 5.5.X is not supported" in {
      givenPlatformSetTo(Windows)
      v5_5_40.asInDownloadPath must throwA[UnsupportedPlatformException]
    }

    "solaris is not supported" in {
      givenPlatformSetTo(Solaris)
      v5_5_40.asInDownloadPath must throwA[UnsupportedPlatformException]
    }

    "freebsd is not supported" in {
      givenPlatformSetTo(FreeBSD)
      v5_5_40.asInDownloadPath must throwA[UnsupportedPlatformException]
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
