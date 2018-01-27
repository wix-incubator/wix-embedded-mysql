package com.wix.mysql.support

import java.io.{File => JFile}
import java.nio.charset.Charset
import java.util.UUID

import org.apache.commons.io.FileUtils

import scala.reflect.io.File

trait TestResourceSupport {

  def createTempFile(content: String): JFile = {
    testResourceDirExists()
    val file = new JFile(s"$targetDir/classes/${UUID.randomUUID}")
    FileUtils.writeStringToFile(file, content, Charset.forName("UTF-8"))
    file
  }

  private def testResourceDirExists() {
    val f = File(s"$targetDir/classes")
    if (!f.exists || f.isFile)
      throw new RuntimeException("Could not generate file in test resources")
  }

  private val targetDir = {
    val target = new JFile(getClass.getProtectionDomain.getCodeSource.getLocation.getFile).getParentFile
    if (!File(s"$target/classes").exists) new JFile("target") else target
  }
}