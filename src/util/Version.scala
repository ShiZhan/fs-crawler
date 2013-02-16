/**
 *
 */
package util

import java.io._
/**
 * @author ShiZhan
 * 2013
 * getting program version from git repository
 */
object Version {

	def getVersion(): String = {
    // get version information
    var version = "not available"
    val masterHashFile = new File("../.git/refs/heads/master")
    if(masterHashFile.exists()) {
      version = (new BufferedReader(new FileReader(masterHashFile))).readLine()
    }
    return version
  }

}
