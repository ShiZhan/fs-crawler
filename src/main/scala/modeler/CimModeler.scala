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

  val local = CimModeler.key + ".owl"
  val base = "https://sites.google.com/site/ontology2013/" + local
  val ns = base + "#"

  private val model = ModelFactory.createDefaultModel
  val Import = model.createResource(base)

  /*
   * directory vocabulary
   */
  // class
  val Object = model.createResource(ns + "Object")

  // object property
  val contain = model.createProperty(ns + "contain")

  // data type property
  val name = model.createProperty(ns + "name")
  val size = model.createProperty(ns + "size")
  val lastModified = model.createProperty(ns + "lastModified")
  val canRead = model.createProperty(ns + "canRead")
  val canWrite = model.createProperty(ns + "canWrite")
  val canExecute = model.createProperty(ns + "canExecute")

  val isDirectory = model.createProperty(ns + "isDirectory")

}

object CimModeler extends Modeler with Logging {

  override val key = "cim"

  override val usage = "Translate directory structure into TriGraM model"

  def tBox = {
    logger.info("translate TBox for CIM specification")
  }

  def aBox(input: String, output: String) = {
    logger.info("translate ABox from [" + input + "] to [" + output + "]")
  }

}