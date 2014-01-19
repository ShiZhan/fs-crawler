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
  def get = BuildIn.getStringOrElse("master", "internal version").trim
}