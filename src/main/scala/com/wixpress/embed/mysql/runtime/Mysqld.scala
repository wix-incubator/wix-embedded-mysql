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
    val baseDir = s"${extractedFiles.generatedBaseDir()}"

    List[String](
      extractedFiles.executable().getAbsolutePath,
      s"--basedir=$baseDir",
      s"--datadir=$baseDir/data",
      s"--plugin-dir=$baseDir/lib/plugin",
      s"--pid-file=${pidFile.getAbsolutePath}",
      s"--port=${config.port}"
    )
  }

}
