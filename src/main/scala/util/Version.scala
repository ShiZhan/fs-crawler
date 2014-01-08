/**
 * Get program version
 */
package util

/**
 * @author ShiZhan
 * get program version from GIT repository
 * use build-in string if not available
 */
object Version {
  def get =
    try {
      val masterHashFile = getClass.getResourceAsStream("../master")
      io.Source.fromInputStream(masterHashFile).mkString
    } catch {
      case e: Exception => "version 0.1 beta (source repo not available)"
    }
}