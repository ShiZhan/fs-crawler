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
  def iterator =
    io.Source.fromFile(csvFile).getLines.map { _.split(delimiter) }
}