package com.wix.mysql.support

import de.flapdoodle.embed.process.io.directories.UserHome
import org.apache.commons.io.FileUtils.deleteDirectory
import org.littleshoot.proxy.{ActivityTrackerAdapter, FlowContext, HttpProxyServer}
import org.littleshoot.proxy.impl.DefaultHttpProxyServer

trait HttpProxyServerSupport {

  def withProxyOn[T](port: Int)(f: (ConnectedActivityTracker, Int) => T): T = {
    def cleanDownloadedFiles() = deleteDirectory(new UserHome(".embedmysql").asFile())
    val tracker = new ConnectedActivityTracker()
    val proxyBootstrap = DefaultHttpProxyServer
      .bootstrap()
      .plusActivityTracker(tracker)
      .withPort(port)

    var proxyServer: Option[HttpProxyServer] = None

    try {
      proxyServer = Some(proxyBootstrap.start())
      cleanDownloadedFiles()
      f(tracker, port)
    } finally {
      proxyServer.map(_.stop())
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

