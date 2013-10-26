/**
 *
 */
package util

import scala.util.Properties.{ envOrElse, userDir }

/**
 * @author ShiZhan
 * configuration information
 * TGMROOT: TriGraM program root
 * TGMDATA: TriGraM program data
 * CIMDATA: CIM Schema Vocabulary and Model
 *
 * NOTE:
 * if above variables are not defined/exported, current directory will be used.
 */
object Config {
  val TGMROOT = envOrElse("TGM_ROOT", userDir) + "/"
  val TGMDATA = envOrElse("TGM_DATA", userDir) + "/.trigram"
  val CIMDATA = TGMROOT + "cim/"
}