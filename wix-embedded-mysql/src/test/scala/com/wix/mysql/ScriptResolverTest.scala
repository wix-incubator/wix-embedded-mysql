package com.wix.mysql

import java.util

import com.wix.mysql.ScriptResolver._
import org.specs2.matcher.{FileMatchers, Matcher}
import org.specs2.mutable.SpecificationWithJUnit

import scala.collection.convert.decorateAsScala._

class ScriptResolverTest extends SpecificationWithJUnit with FileMatchers {
  val contentsOf001Init = "create table t1 "
  val contentsOf002Update = "create table t2 "
  val contentsOf003Update = "create table t3 "
  val contentsOf004Update = "create table t1 "
  val contentsOf001InitInJar = "create table t1_jar"
  val contentsOf002UpdateInJar = "create table t2_jar"

  "classPathFile" should {

    "resolve a single classPath file" in {
      classPathFile("/db/001_init.sql") must beAScriptWith(contentsOf001Init)
    }

    "resolve a single classPath file without preceding '/'" in {
      classPathFile("db/001_init.sql") must beAScriptWith(contentsOf001Init)
    }

    "throw a ScriptResolutionException for a non-existent script" in {
      classPathFile("db/not-exists.sql") must throwA[ScriptResolutionException]
    }
  }

  "classPathFiles" should {

    "resolve multiple classPath files" in {
      classPathFiles("/db/*.sql") must containScripts(
        contentsOf001Init,
        contentsOf002Update,
        contentsOf003Update,
        contentsOf004Update)
    }

    "resolve multiple classPath files without preceding '/'" in {
      classPathFiles("db/*.sql") must containScripts(
        contentsOf001Init,
        contentsOf002Update,
        contentsOf003Update,
        contentsOf004Update)
    }

    "throw a ScriptResolutionException if no classPathFiles are found" in {
      classPathFiles("does-not-exist/*.sql") must throwA[ScriptResolutionException]
    }
  }

  "classPathScript" should {

    "resolve a single classPath file" in {
      classPathScript("/db/001_init.sql") must beAScriptWith(contentsOf001Init)
    }

    "resolve a single classPath file without preceding '/'" in {
      classPathScript("db/001_init.sql") must beAScriptWith(contentsOf001Init)
    }

    "resolve a single classPath file within packaged jar" in {
      classPathScript("/db-jar/001_jar-init.sql") must beAScriptWith(contentsOf001InitInJar)
    }

    "resolve a single classPath file within packaged jar without preceding '/'" in {
      classPathScript("db-jar/001_jar-init.sql") must beAScriptWith(contentsOf001InitInJar)
    }

    "throw a ScriptResolutionException for a non-existent script" in {
      classPathScript("db/not-exists.sql") must throwA[ScriptResolutionException]
    }
  }

  "classPathScripts" should {

    "resolve multiple classPath files" in {
      classPathScripts("/db/*.sql") must containScripts(
        contentsOf001Init,
        contentsOf002Update,
        contentsOf003Update,
        contentsOf004Update)
    }

    "resolve multiple classPath files without preceding '/'" in {
      classPathScripts("db/*.sql") must containScripts(
        contentsOf001Init,
        contentsOf002Update,
        contentsOf003Update,
        contentsOf004Update)
    }

    "resolve multiple classPath scripts within jar in classpath" in {
      classPathScripts("/db-jar/*.sql") must containScripts(contentsOf001InitInJar, contentsOf002UpdateInJar)
    }

    "resolve multiple classPath scripts within jar in classpath without preceding '/'" in {
      classPathScripts("/db-jar/*.sql") must containScripts(contentsOf001InitInJar, contentsOf002UpdateInJar)
    }

    "throw a ScriptResolutionException if no classPathFiles are found" in {
      classPathScripts("does-not-exist/*.sql") must throwA[ScriptResolutionException]
    }
  }

  def aScriptWith(fragment: String): Matcher[SqlScriptSource] =
    beAScriptWith(fragment)

  def beAScriptWith(fragment: String): Matcher[SqlScriptSource] =
    startWith(fragment) ^^ {
      (_: SqlScriptSource).read aka "script fragment mismatch"
    }

  def containScripts(fragments: String*): Matcher[util.List[SqlScriptSource]] =
    contain(exactly(fragments.map(aScriptWith): _*)).inOrder ^^ {
      (_: java.util.List[SqlScriptSource]).asScala
    }

}
