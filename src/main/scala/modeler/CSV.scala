/**
 * Modeler to translate CSV file into triple model
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
 * Translate CSV into triple model
 * A wide range support for various data sources that can be represented by CSV
 */
object CSV extends Modeler with Logging {
  override val key = "csv"

  override val usage = "<CSV> => [triples]"

  def run(options: Array[String]) = {
    val input = options(0)

  }

}