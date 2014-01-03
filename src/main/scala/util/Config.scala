/**
 *
 */
package util

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
  import java.io.File
  import scala.util.Properties.{ envOrElse, userDir }

  def UNC(fileName: String) = {
    val f = new File(fileName)
    f.getAbsolutePath
  }

  val _PWD = userDir
  val TGMROOT = UNC(envOrElse("TGM_ROOT", _PWD))
  val TGMDATA = UNC(envOrElse("TGM_DATA", _PWD) + "/.trigram")
  val CIMDATA = UNC(envOrElse("CIM_DATA", _PWD) + "/cim")
}