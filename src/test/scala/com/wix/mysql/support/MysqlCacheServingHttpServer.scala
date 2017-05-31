package com.wix.mysql.support

import java.io.{BufferedInputStream, File}

import com.wix.mysql.support.MysqlCacheServingHttpServer.PORT
import fi.iki.elonen.router.RouterNanoHTTPD

class MysqlCacheServingHttpServer extends RouterNanoHTTPD(PORT) {
  val port = PORT

  val embeddedMysqlCacheDir: File = new File(System.getProperty("user.home"), ".embedmysql").getAbsoluteFile
  addRoute("/(.)+", classOf[StaticPageTestHandler], embeddedMysqlCacheDir)
}

class StaticPageTestHandler extends RouterNanoHTTPD.StaticPageHandler {
  protected override def fileToInputStream(fileOrdirectory: File): BufferedInputStream = {
    super.fileToInputStream(fileOrdirectory)
  }
}

object MysqlCacheServingHttpServer {
  val PORT = 3000
}