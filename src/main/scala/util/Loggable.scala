/**
 * slf4j wrapper
 */
package util

/**
 * @author ShiZhan
 * for use slf4j with object
 */
trait Logging {
  val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)
}