/**
 * Get program version
 */
package util

/**
 * @author ShiZhan
 * 2013
 * get program version from GIT repository
 */
object Version {

  private val masterHashFilePath = ".git/refs/heads/master"
  private val masterHashFileExists = new java.io.File(masterHashFilePath).exists

  def getVersion =
    if (masterHashFileExists)
      io.Source.fromFile(masterHashFilePath).getLines.mkString
    else
      "not available"

}
