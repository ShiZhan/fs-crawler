/**
 * Modeler to translate file content into (checksum, position) model
 */
package modeler

import java.io.{ File, FileOutputStream }
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary.{
  RDF,
  RDFS,
  OWL,
  OWL2,
  DC_11 => DC,
  DCTerms => DT
}
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import util.{ Logging, Version, DateTime, Hash }

import modeler.{ CimVocabulary => CIM }

/**
 * @author ShiZhan
 * Translate content characters of 'virtually' any data source into
 * structural model item(checksum, size, path, [item, ...]) for easy comparison
 */
object Checksum extends Modeler with Logging {
  override val key = "chk"

  override val usage = "[file] into [structural checksum group]"

  def run(input: String, output: String) = {
    val f = new File(input)
    if (!f.exists)
      logger.error("input item does not exist")
    else if (!f.isFile)
      logger.error("input item is not file")
    else {
      logger.info("Model file [{}]", f.getAbsolutePath)

      val base = f.toURI.toString
      val ns = base + "#"

      val m = ModelFactory.createDefaultModel
      
    }
  }
}