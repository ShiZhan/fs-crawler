/**
 * Text related functions
 */
package helper

/**
 * @author ShiZhan
 * Text related functions
 * 1. fromFile, toFile: read/write all lines to/from List from/to text file
 * 2. StringWriter: write string to text file by buffered writer
 */
object Strings {
  import java.io.{ File, PrintWriter }

  def fromFile(fileName: String) = {
    val buf = io.Source.fromFile(new File(fileName))
    val lines = buf.getLines.toList
    buf.close
    lines
  }

  implicit class Strings[T](lines: Seq[T]) {
    def toFile(fileName: String) = {
      val p = new PrintWriter(new File(fileName))
      lines.foreach(p.println)
      p.close
    }
  }
}