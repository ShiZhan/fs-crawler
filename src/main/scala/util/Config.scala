/**
 *
 */
package util

import scala.util.Properties.{ envOrElse, userDir }

/**
 * @author ShiZhan
 * configuration information
 * TGMROOT: TriGraM program root
 */
object Config {
  val TGMROOT = envOrElse("TGM_ROOT", userDir) + "/"
}