package com.wix.mysql.v3

import java.io.File

import com.wix.mysql.v3.ClassPathScriptResolver.ScriptResolutionException
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

/**
 * Helper for locating schema init scripts in a classpath
 */
object ClassPathScriptResolver {

  /**
   * Locates a single file in a classpath
   */
  def file(path: String): File = {
    val normalizedPath =  if (path.startsWith("/")) path else s"/$path"

    val resource = getClass.getResource(normalizedPath)
    if (resource == null)
      throw new ScriptResolutionException(normalizedPath)
    new File(resource.toURI)
  }

  /**
   * Locates files matching pattern, ordered using natural aphanumeric order
   *
   * Example: db/\*.sql
   */
  def files(pattern: String): Seq[File] = ???

  class ScriptResolutionException(path: String)
    extends RuntimeException(s"No script(s) found for path '$path'")
}
