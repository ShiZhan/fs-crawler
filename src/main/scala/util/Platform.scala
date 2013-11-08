/**
 * check platform information
 */
package util

/**
 * @author ShiZhan
 * gather platform information
 */
object Platform {
  val hostname = java.net.InetAddress.getLocalHost.getHostName
  val os = System.getProperty("os.name")
  val isWindows = os.startsWith("Windows")
  val javaVer = System.getProperty("java.version")
  val scalaVer = scala.util.Properties.versionMsg
}