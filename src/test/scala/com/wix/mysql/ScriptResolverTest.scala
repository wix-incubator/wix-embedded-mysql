package com.wix.mysql

import java.io.File

import com.wix.mysql.ScriptResolver.{ScriptResolutionException, classPathFile, classPathFiles}
import org.specs2.matcher.FileMatchers
import org.specs2.mutable.SpecWithJUnit

import scala.collection.convert.decorateAsScala._

class ScriptResolverTest extends SpecWithJUnit with FileMatchers {
  val contentsOf001Init = "create table t1"
  val contentsOf002Update = "create table t2"
  val contentsOf003Update = "create table t3"

  //TODO: add additional variations for sorting, etc.
  "ScriptResolver.classPathFile" should {

    "resolve a single classPath file" in {
      classPathFile("/db/001_init.sql").read() must startWith(contentsOf001Init)
    }

    "resolve a single classPath file without preceding '/'" in {
      classPathFile("db/001_init.sql").read() must startWith(contentsOf001Init)
    }

    "throw a ScriptResolutionException for a non-existent script" in {
      classPathFile("db/not-exists.sql") must throwA[ScriptResolutionException]
    }
  }

  "ScriptResolver.classPathFiles" should {

    "resolve multiple classPath files" in {
      classPathFiles("/db/*.sql").asScala.map(_.read) must contain(exactly(
        startWith(contentsOf001Init),
        startWith(contentsOf002Update),
        startWith(contentsOf003Update))).inOrder
    }

    "resolve multiple classPath files without preceding '/'" in {
      classPathFiles("/db/*.sql").asScala.map(_.read) must contain(exactly(
        startWith(contentsOf001Init),
        startWith(contentsOf002Update),
        startWith(contentsOf003Update))).inOrder
    }

    "throw a ScriptResolutionException if no classPathFiles are found" in {
      classPathFiles("does-not-exist/*.sql") must throwA[ScriptResolutionException]
    }
  }

  def aFile(name: String) = new File(getClass.getResource(name).toURI)
}
