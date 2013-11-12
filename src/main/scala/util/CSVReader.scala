/**
 * CSV Reader
 */
package util

import java.io.File

/**
 * @author ShiZhan
 * CSV Reader
 */
class CSVReader(csvFile: File, delimiter: Char) {
  private val f = io.Source.fromFile(csvFile)
  private val lines = f.getLines
  val iterator = f.getLines.map { _.split(delimiter) }
}