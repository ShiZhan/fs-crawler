/**
 * check platform information
 */
package helper

/**
 * @author ShiZhan
 * gather platform information
 */
object Platform {
  val HOSTNAME = java.net.InetAddress.getLocalHost.getHostName
  val OS = System.getProperty("os.name")
  val isWindows = OS.startsWith("Windows")
}