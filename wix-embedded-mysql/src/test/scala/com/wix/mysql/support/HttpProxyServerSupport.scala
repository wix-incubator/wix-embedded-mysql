package com.wix.mysql.support

import java.io.{File, FileInputStream}
import java.nio.file.Files

import com.wix.mysql.PackagePaths
import com.wix.mysql.distribution.Version
import de.flapdoodle.embed.process.distribution.Distribution
import de.flapdoodle.embed.process.io.directories.UserHome
import fi.iki.elonen.NanoHTTPD
import org.littleshoot.proxy.impl.DefaultHttpProxyServer
import org.littleshoot.proxy.{ActivityTrackerAdapter, FlowContext, HttpProxyServer}

trait HttpProxyServerSupport {
  val tmpDir: File = new UserHome(".embedmysql").asFile()

  def withProxyOn[T](proxyPort: Int, targetPort: Int, servedVersion: Version)(f: (ConnectedActivityTracker, Int, Int, Version) => T): T = {
    def copyDownloadedFile() = {
      val source = new File(tmpDir, new PackagePaths().getPath(Distribution.detectFor(servedVersion)))
      val target = new File(tmpDir, "to-download.tar.gz")
      if (!target.exists()) {
        Files.copy(source.toPath, target.toPath)
      }
      Files.delete(source.toPath)
      target
    }

    def copyBack() = {
      val source = new File(tmpDir, "to-download.tar.gz")
      val target = new File(tmpDir, new PackagePaths().getPath(Distribution.detectFor(servedVersion)))

      if (!target.exists()) {
        Files.copy(source.toPath, target.toPath)
      }
      Files.delete(source.toPath)
    }


    val FileToServe = copyDownloadedFile()

    val tracker = new ConnectedActivityTracker()
    val proxyBootstrap = DefaultHttpProxyServer
      .bootstrap()
      .plusActivityTracker(tracker)
      .withPort(proxyPort)

    var proxyServer: Option[HttpProxyServer] = None
    var staticsServer: Option[StaticsServer] = None

    try {
      staticsServer = {
        val srv = new StaticsServer(targetPort, FileToServe)
        srv.start()
        Some(srv)
      }
      proxyServer = Some(proxyBootstrap.start())

      f(tracker, proxyPort, targetPort, servedVersion)
    } finally {
      proxyServer.foreach(_.stop())
      staticsServer.foreach(_.stop())
      copyBack()
    }
  }
}

class ConnectedActivityTracker extends ActivityTrackerAdapter {
  val fiftyMegabytesInBytes = 50000000
  var bytesSent: Int = 0

  override def bytesSentToClient(flowContext: FlowContext, numberOfBytes: Int): Unit = {
    bytesSent += numberOfBytes
  }

  def wasDownloaded: Boolean = bytesSent > fiftyMegabytesInBytes
}

class StaticsServer(port: Int, fileToServe: File) extends NanoHTTPD(port) {

  override def serve(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response = {
    NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.ACCEPTED, "application/tar+gzip", new FileInputStream(fileToServe))// .newFixedLengthResponse("qwe")
  }
}