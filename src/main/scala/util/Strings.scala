/**
 * Text related functions
 */
package util

import java.io.{ File, PrintWriter }

/**
 * @author ShiZhan
 * Text related functions
 * 1. toFile: write all lines from List to text file
 * 2. fromFile: read all lines to List from text file
 */
class Strings[T](lines: Seq[T]) {
  def toFile(fileName: String) = {
    val f = new File(fileName)
    val p = new PrintWriter(f)
    lines.foreach(p.println)
    p.close
  }
}

object Strings {
  implicit def strings[T](lines: Seq[T]) = new Strings(lines)

  def fromFile(fileName: String) = {
    val f = new File(fileName)
    val buf = io.Source.fromFile(f)
    val lines = buf.getLines.toList
    buf.close
    lines
  }
}