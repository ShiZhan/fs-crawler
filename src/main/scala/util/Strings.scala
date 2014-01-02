/**
 * Text related functions
 */
package util

/**
 * @author ShiZhan
 * Text related functions
 * 1. fromFile: read all lines to List from text file
 * 2. toFile: write all lines from List to text file
 */
object Strings {
  import java.io.{ File, PrintWriter }

  def fromFile(fileName: String) = {
    val f = new File(fileName)
    val buf = io.Source.fromFile(f)
    val lines = buf.getLines.toList
    buf.close
    lines
  }

  implicit class Strings[T](lines: Seq[T]) {
    def toFile(fileName: String) = {
      val f = new File(fileName)
      val p = new PrintWriter(f)
      lines.foreach(p.println)
      p.close
    }
  }
}