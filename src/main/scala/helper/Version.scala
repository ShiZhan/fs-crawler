/**
 * Get program version
 */
package helper

/**
 * @author ShiZhan
 * get program version from GIT repository
 * use build-in string if not available
 */
object Version {
  def get =
    try {
      val masterHashFile = getClass.getClassLoader.getResourceAsStream("master")
      io.Source.fromInputStream(masterHashFile).mkString.trim
    } catch {
      case e: Exception => "internal experimental version"
    }
}