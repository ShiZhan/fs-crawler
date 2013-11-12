/**
 * Text related functions
 */
package util

import java.io.File

/**
 * @author ShiZhan
 * Text related functions
 * 1. read all lines to List from text file
 */
object Text {
  def readAllLines(fileName: String) = {
    val f = new File(fileName)
    val buf = io.Source.fromFile(f)
    val lines = buf.getLines.toList
    buf.close
    lines
  }
}