/**
 * plain text CSV modeler
 */
package modeler

import java.io.{ FileReader, FileOutputStream }
import scala.collection.JavaConversions._
import au.com.bytecode.opencsv.CSVReader
import util.{ Logging, Version, DateTime, Hash }

/**
 * @author ShiZhan
 * plain text CSV translator for handling large document
 */
object CSVex extends Modeler with Logging {
  override val key = "csvex"

  override val usage = "<CSV> <schema> => [triples], plain text translation for massive document."

  def run(options: Array[String]) = {
    val input = options(0)
    val reader = new CSVReader(new FileReader(input), ' ')
    val entries = Iterator.continually { reader.readNext }.takeWhile(_ != null)
    if (entries != null) {
      entries.foreach(i => println(i.mkString(", ")))
    }
    reader.close
  }
}