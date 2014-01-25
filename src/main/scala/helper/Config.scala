/**
 *
 */
package helper

/**
 * @author ShiZhan
 * configuration information
 * TGMROOT: TriGraM program root
 * TGMDATA: TriGraM program data
 * CIMDATA: CIM Schema Vocabulary and Models
 *
 * NOTE:
 * if above variables are not defined/exported, current directory will be used.
 */
object Config {
  import java.io.File
  import scala.util.Properties.{ envOrElse, userDir }

  val _PWD = userDir
  val tgmRoot = new File(envOrElse("TGM_ROOT", _PWD))
  val TGMROOT = tgmRoot.getAbsolutePath
  val tgmData = new File(envOrElse("TGM_DATA", _PWD) + "/.trigram")
  val TGMDATA = tgmData.getAbsolutePath

  if (!tgmData.exists) tgmData.mkdir
}