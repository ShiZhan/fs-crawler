/**
 *
 */
import java.io._
/**
 * @author ShiZhan
 * 2013
 * supporting functions
 */
package object util {

	def getVersion(): String = {
		// get version information
    var version = "not available"
    val masterHashFile = new File("../.git/refs/heads/master")
    if(masterHashFile.exists()) {
      val reader = new BufferedReader(new FileReader(masterHashFile))
      version = reader.readLine()
    }
    return version
	}
}