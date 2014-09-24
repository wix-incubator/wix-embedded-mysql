package com.wixpress.embed.mysql.support

import java.util.logging.Logger

/**
 * @author viliusl
 * @since 24/09/14
 */
trait LoggingSupport {
  val log = Logger.getLogger(getClass.getName)
}
