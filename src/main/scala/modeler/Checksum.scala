/**
 * Modeler to translate file content into (checksum, position) model
 */
package modeler

import java.io.{ File, FileOutputStream }
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary.{ RDF, RDFS, OWL, OWL2, DC_11 => DC, DCTerms => DT }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import util.{ Logging, Version, DateTime, Hash }

import modeler.{ CimVocabulary => CIM }

/**
 * @author ShiZhan
 * Translate content characteristics of 'virtually' any data source into
 * structural model item(checksum, size, path, [item, ...]) for easy comparison
 */
object Checksum extends Modeler with Logging {
  override val key = "chk"

  override val usage = "<file> <chunk size> => [structural checksum group]"

  def run(options: Array[String]) = {
    options.toList match {
      case input :: chunkSize :: tail => translate(input, chunkSize)
      case _ => logger.error("parameter error: [{}]", options)
    }
  }

  def toInt(s: String): Option[Int] = {
    try {
      Some(s.toInt)
    } catch {
      case e: Exception => None
    }
  }

  private def translate(input: String, chunkSize: String) = {
    val f = new File(input)
    if (!f.exists)
      logger.error("input source does not exist")
    else if (!f.isFile)
      logger.error("input source is not file")
    else {
      logger.info("Model file [{}]", f.getAbsolutePath)

      val c = toInt(chunkSize).getOrElse(65536)

      val base = f.toURI.toString
      val ns = base + "#"

      val m = ModelFactory.createDefaultModel

    }
  }
}