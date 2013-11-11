/**
 *
 */
package util

import java.io.File
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
  def UNC(fileName: String) = {
    val f = new File(fileName)
    val uri = f.toURI.toString
    if (Platform.isWindows)
      f.toURI.toString.replaceFirst("file:/", "")
    else
      f.toURI.toString.replaceFirst("file:", "")
  }

  val _PWD = userDir
  val _TGMROOT = envOrElse("TGM_ROOT", _PWD)
  val TGMROOT = UNC(_TGMROOT)
  val TGMDATA = UNC(envOrElse("TGM_DATA", _PWD)) + ".trigram"
  val CIMDATA = UNC(envOrElse("CIM_DATA", _TGMROOT)) + "cim/"
}