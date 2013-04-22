/**
 *
 */
package modeler

import scala.xml.XML
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

    val i = XML.loadFile(input).toSeq
    val classes = i \\ "VALUE.OBJECT" \ "CLASS"
    val rNodes = classes.flatMap(c => c \ "PROPERTY.REFERENCE")
    val pNodes = classes.flatMap(c => c \ "PROPERTY" ++ c \ "PROPERTY.ARRAY")
    val rNames = rNodes.map(r => (r \ "@NAME").toString)
    val pNames = pNodes.map(p => (p \ "@NAME").toString)
    val objProp = rNames.distinct
    val datProp = pNames.distinct

    logger.info("[%d] classes [%d] object properties [%d] data type properties".
      format(classes.length, objProp.length, datProp.length))

    val o = ModelFactory.createDefaultModel

    if (o.isEmpty)
      logger.info("Nothing translated")
    else {
      o.write(new java.io.FileOutputStream(output), "RDF/XML-ABBREV")

      logger.info("[%d] triples generated".format(o.size))
    }
  }

}