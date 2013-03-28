/**
 * Get program version
 */
package util

import scala.io.Source
import java.io.File
/**
 * @author ShiZhan
 * 2013
 * get program version from GIT repository
 */
object Version {

  private val masterHashFilePath = ".git/refs/heads/master"
  private val masterHashFileExists = (new File(masterHashFilePath)).exists

  def getVersion =
    if (masterHashFileExists)
      Source.fromFile(masterHashFilePath).getLines.mkString("")
    else
      "not available"

}
