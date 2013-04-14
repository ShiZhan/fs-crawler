/**
 * slf4j wrapper
 */
package util

import org.slf4j.{ Logger, LoggerFactory }

/**
 * @author ShiZhan
 * for use slf4j with object
 * Note: implementation taken from scalax.logging API
 */
trait Logging {
  val logger = Logging.getLogger(this)
}

object Logging {

  def loggerNameForClass(className: String) =
    if (className endsWith "$")
      className.substring(0, className.length - 1)
    else
      className

  def getLogger(logging: AnyRef) = LoggerFactory.getLogger(loggerNameForClass(logging.getClass.getName))

}