package com.wix.mysql

import java.io.File

import com.wix.mysql.ScriptResolver.ScriptResolutionException
import org.specs2.mutable.SpecWithJUnit

import scala.collection.convert.decorateAsScala._

/**
 * @author viliusl
 * @since 06/06/15
 */
class ScriptResolverTest extends SpecWithJUnit {

  //TODO: add additional variations for sorting, etc.
  "ScriptResolver.classPathFile" should {

    "resolve a single classPath file" in {
      ScriptResolver.classPathFile("/db/001_init.sql").exists must beTrue
    }

    "resolve a single classPath file without preceding '/'" in {
      ScriptResolver.classPathFile("db/001_init.sql").exists must beTrue
    }

    "throw a ScriptResolutionException for a non-existsent script" in {
      ScriptResolver.classPathFile("db/not-exists.sql") must throwA[ScriptResolutionException]
    }
  }

  "ScriptResolver.classPathFiles" should {

    "resolve multiple classPath files" in {
      ScriptResolver.classPathFiles("/db/*.sql").asScala mustEqual Seq(
        aFile("/db/001_init.sql"),
        aFile("/db/002_update1.sql"),
        aFile("/db/003_update2.sql"))
    }

    "resolve multiple classPath files without preceding '/'" in {
      ScriptResolver.classPathFiles("db/*.sql").asScala mustEqual Seq(
        aFile("/db/001_init.sql"),
        aFile("/db/002_update1.sql"),
        aFile("/db/003_update2.sql"))
    }

    "throw a ScriptResolutionException if no classPathFiles are found" in {
      ScriptResolver.classPathFiles("does-not-exist/*.sql") must throwA[ScriptResolutionException]
    }
  }

  def aFile(name: String) = new File(getClass.getResource(name).toURI)
}
