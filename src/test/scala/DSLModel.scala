/**
 *
 */

import modeler.DSL._
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary.{ RDF, RDFS, OWL, OWL2, DC_11 => DC, DCTerms => DT }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import util.{ Version, DateTime }

/**
 * @author ShiZhan
 *
 */
object DSLModel {

  def main(args: Array[String]): Unit = {
    val model = ModelFactory.createDefaultModel
    val m = DSLElement(model)
    val base = "http://localhost/test"

    m ++ (base, OWL.Ontology) -- (DC.date) --> (DateTime.get, XSDdateTime) -- (DC.description) --> ("test model", XSDstring) -- (DT.license) --> ("Apache 2", XSDstring) -- (OWL.versionInfo) --> (Version.get, XSDstring)

    println(m)
  }
}