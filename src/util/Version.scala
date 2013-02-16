/**
 * Get program version
 */
package util

import java.io._
/**
 * @author ShiZhan
 * 2013
 * get program version from git repository
 */
object Version {

	def getVersion(): String = {
    val masterHashFile = new File("../.git/refs/heads/master")
    return if (masterHashFile.exists())
        (new BufferedReader(new FileReader(masterHashFile))).readLine()
      else
        "not available"
  }

}
