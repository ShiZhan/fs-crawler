/**
 * check platform information
 */
package util

/**
 * @author ShiZhan
 * gather platform information
 */
object Platform {
  val HOSTNAME = java.net.InetAddress.getLocalHost.getHostName
  val OS = System.getProperty("os.name")
  val isWindows = OS.startsWith("Windows")
  val JAVAVER = System.getProperty("java.version")
  val SCALAVER = scala.util.Properties.versionMsg
}