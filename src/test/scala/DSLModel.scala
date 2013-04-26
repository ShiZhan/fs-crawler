/**
 * DSL test
 */

import modeler.DSL._
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary.{ RDF, RDFS, OWL, OWL2, DC_11 => DC, DCTerms => DT }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import util.{ Version, DateTime }

/**
 * @author ShiZhan
 * DSL test object
 */
object DSLModel {

  def main(args: Array[String]): Unit = {
    val model = ModelFactory.createDefaultModel
    val m = DSLElement(model)
    val base = "http://localhost/test"

    val r = m ++ (base, OWL.Ontology)
    r -- (DC.date) --> (DateTime.get, XSDdateTime)
    r -- (DC.description) --> ("test model", XSDstring)
    r -- (DT.license) --> ("Apache 2", XSDstring)
    r -- (OWL.versionInfo) --> (Version.get, XSDstring)

    println(m)
  }

}