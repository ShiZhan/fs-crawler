/**
 * check platform information
 */
package util

/**
 * @author ShiZhan
 * check platform information
 */
object Platform {
  import System.getProperty
  val os = getProperty("os.name")
  val isWin = os.startsWith("Windows")
  val javaVer = getProperty("java.version")
  val scalaVer = scala.util.Properties.versionMsg
}