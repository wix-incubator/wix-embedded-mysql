package com.wix.mysql.v3

import java.io.File

import com.wix.mysql.ClassPathScriptResolver
import com.wix.mysql.ClassPathScriptResolver.ScriptResolutionException
import org.specs2.mutable.SpecWithJUnit

/**
 * @author viliusl
 * @since 06/06/15
 */
class ClassPathScriptResolverTest extends SpecWithJUnit {

  "ClassPathScriptResolver.file" should {
    "resolve a single file" in {
      ClassPathScriptResolver.file("/db/001_init.sql").exists must beTrue
    }

    "resolve a single file without preceding '/'" in {
      ClassPathScriptResolver.file("db/001_init.sql").exists must beTrue
    }

    "throw a ScriptNotFound exception for a non-existsent script" in {
      ClassPathScriptResolver.file("db/not-exists.sql") must throwA[ScriptResolutionException]
    }
  }

  "ClassPathScriptResolver.files" should {

    "resolve multiple files" in {
      ClassPathScriptResolver.files("db/*.sql") mustEqual Seq(
        aFile("db/001_init.sql"),
        aFile("db/002_update1.sql"),
        aFile("db/003_update2.sql")
      )
    }

    "throw a ScriptNotFound if no files are found" in {
      ClassPathScriptResolver.files("does-not-exist/*.sql") must throwA[ScriptResolutionException]
    }
  }

  def aFile(name: String) = new File(getClass.getResource(name).toURI)
}
