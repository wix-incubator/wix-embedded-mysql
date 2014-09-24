package com.wixpress.embed.mysql.runtime

import java.io.File

import com.wixpress.embed.mysql.config.MysqldConfig
import de.flapdoodle.embed.process.extract.IExtractedFileSet
import collection.JavaConversions._
/**
 * @author viliusl
 * @since 18/09/14
 */
object Mysqld {

  def getCommandLine(config: MysqldConfig, extractedFiles: IExtractedFileSet, pidFile: File) : java.util.List[String] = {
    //FIXME: ouch
    val baseDir = s"${extractedFiles.generatedBaseDir()}/mysql-5.6.21-osx10.8-x86_64"

    List[String](
      extractedFiles.executable().getAbsolutePath,
      s"--basedir=$baseDir",
      s"--datadir=$baseDir/data",
      s"--plugin-dir=$baseDir/lib/plugin",
      s"--log-error=$baseDir/data/localhost.err",
      s"--pid-file=$baseDir/data/localhost.pid"
    )
  }

}
