package com.wixpress.embed.mysql.support

import org.slf4j.LoggerFactory

/**
 * @author viliusl
 * @since 24/09/14
 */
trait LoggingSupport {
  lazy val log = LoggerFactory.getLogger(this.getClass)
}
