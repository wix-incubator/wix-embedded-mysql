package com.wix.mysql

import java.io.File

import com.wix.mysql.ScriptResolver.{classPathFiles, classPathFile, ScriptResolutionException}
import org.specs2.matcher.FileMatchers
import org.specs2.mutable.SpecWithJUnit

import scala.collection.convert.decorateAsScala._

/**
 * @author viliusl
 * @since 06/06/15
 */
class ScriptResolverTest extends SpecWithJUnit with FileMatchers {

  //TODO: add additional variations for sorting, etc.
  "ScriptResolver.classPathFile" should {

    "resolve a single classPath file" in {
      classPathFile("/db/001_init.sql") must beAFile
    }

    "resolve a single classPath file without preceding '/'" in {
      classPathFile("db/001_init.sql")must beAFile
    }

    "throw a ScriptResolutionException for a non-existent script" in {
      classPathFile("db/not-exists.sql") must throwA[ScriptResolutionException]
    }
  }

  "ScriptResolver.classPathFiles" should {

    "resolve multiple classPath files" in {
      classPathFiles("/db/*.sql").asScala mustEqual Seq(
        aFile("/db/001_init.sql"),
        aFile("/db/002_update1.sql"),
        aFile("/db/003_update2.sql"))
    }

    "resolve multiple classPath files without preceding '/'" in {
      classPathFiles("db/*.sql").asScala mustEqual Seq(
        aFile("/db/001_init.sql"),
        aFile("/db/002_update1.sql"),
        aFile("/db/003_update2.sql"))
    }

    "throw a ScriptResolutionException if no classPathFiles are found" in {
      classPathFiles("does-not-exist/*.sql") must throwA[ScriptResolutionException]
    }
  }

  def aFile(name: String) = new File(getClass.getResource(name).toURI)
}
