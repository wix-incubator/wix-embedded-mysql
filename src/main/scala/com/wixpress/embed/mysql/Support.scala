package com.wixpress.embed.mysql

import java.io.File
import java.io.File

import de.flapdoodle.embed.process.extract.IExtractedFileSet

import scala.io.BufferedSource

/**
 * @author viliusl
 * @since 23/09/14
 */
class Support(files: IExtractedFileSet) {

  def baseDir: File = new File(s"${files.generatedBaseDir()}/mysql-5.6.21-osx10.8-x86_64/")
  def setExec(relPath: String) = absPath(relPath).setExecutable(true)
  def absPath(relPath: String) = new File(baseDir.getAbsolutePath, relPath)

  def stopInstance(): Unit = {
    val cmd = "bin/mysqladmin"
    setExec(cmd)

    val p = Runtime.getRuntime.exec(s"$cmd -uroot -hlocalhost --protocol=tcp shutdown", Array[String](), baseDir)
    val retCode = p.waitFor()

    new BufferedSource(p.getInputStream).getLines().foreach(l => println(s"Out: $l"))
    new BufferedSource(p.getErrorStream).getLines().foreach(l => println(s"Err: $l"))

    if (retCode != 0) throw new RuntimeException(s"mysqld stop command exited with error code: $retCode")
  }

  def initDatabase(): Unit = {
    val cmd = "scripts/mysql_install_db"
    setExec("bin/my_print_defaults")//additional needed command
    setExec(cmd)

    val p = Runtime.getRuntime.exec(s"$cmd --force --no-defaults", Array[String](), baseDir)
    val retCode = p.waitFor()

    new BufferedSource(p.getInputStream).getLines().foreach(l => println(s"Out: $l"))
    new BufferedSource(p.getErrorStream).getLines().foreach(l => println(s"Err: $l"))

    if (retCode != 0) throw new RuntimeException(s"'$cmd' command exited with error code: $retCode")
  }
}

object Support {
  def apply(files: IExtractedFileSet) = new Support(files)
}
