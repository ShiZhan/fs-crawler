/**
 *
 */
package modeler

import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary.{ RDF, RDFS, OWL, OWL2, DC_11 => DC, DCTerms => DT }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import util.{ Logging, Version, DateTime, Hash }

/**
 * @author ShiZhan
 * translate DMTF CIM specification into TriGraM model
 */
object CIM {

  val local = "tgm" + CimModeler.key + ".owl"
  val base = "https://sites.google.com/site/ontology2013/" + local
  val ns = base + "#"

}

object CimModeler extends Modeler with Logging {

  override val key = "cim"

  override val usage = "Translate DMTF CIM schema http://dmtf.org/standards/cim"

  def tBox = {
    logger.info("prepare meta-model for CIM schema translation")

    logger.info("Work in Progress")
  }

  def aBox(input: String, output: String) = {
    logger.info("translate CIM schema from [" + input + "] to [" + output + "]")

    logger.info("Work in Progress")
  }

}