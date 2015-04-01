package com.wix.mysql

import java.io.{ByteArrayOutputStream, PrintStream}

import com.wix.mysql.config.MysqldConfigBuilder
import com.wix.mysql.distribution.Version
import org.specs2.execute.{AsResult, Result}
import org.specs2.specification.{AroundExample, Scope}

/**
 * @author viliusl
 * @since 27/03/15
 */
class SupportedVersionsTest extends IntegrationTest with AroundExample {

  Version.values filter { _.supportsCurrentPlatform() } foreach { version =>
    s"${version} should work on ${System.getProperty("os.name")}" in new Scope {
        startAndVerifyDatabase( new MysqldConfigBuilder(version).build )
    }
  }

  //TODO: Console.withOut(baos) { ... } should work, but did not...
  def around[R: AsResult](r: => R): Result = {
    val baos = new ByteArrayOutputStream()
    val oldOutput = System.out

    try {
      System.setOut(new PrintStream(baos))
      AsResult(r)
    } finally {
      System.setOut(oldOutput)
      println(baos.toString)
      baos.toString must not contain ("Something bad happened.")
    }
  }
}
