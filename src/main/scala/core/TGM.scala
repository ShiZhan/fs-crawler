/**
 * TriGraM core model
 */
package core

import com.hp.hpl.jena.rdf.model.impl.{ ResourceImpl, PropertyImpl }
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary.{ RDF, RDFS, OWL, XSD }
import com.hp.hpl.jena.vocabulary.RDF.{ `type` => TYPE }
import java.io.FileOutputStream
import util.Logging

/**
 * @author ShiZhan
 * TriGraM core model vocabulary, concept and statement
 */
object TGM extends Logging {
  val uri = "https://sites.google.com/site/ontology2013/trigram.owl#"
  val OBJECT = new ResourceImpl(uri + "object")
  val contain = new PropertyImpl(uri + "contain")
  val name = new PropertyImpl(uri + "name")

  def createCore = {
    logger.info("initialize core model")
    val model = ModelFactory.createDefaultModel
    model.setNsPrefix("tgm", uri)
    model.createProperty(contain.getURI)
      .addProperty(TYPE, OWL.ObjectProperty)
    model.createProperty(name.getURI)
      .addProperty(TYPE, OWL.DatatypeProperty)
    model.createResource(OBJECT.getURI)
      .addProperty(TYPE, OWL.Class)
      .addProperty(name, XSD.normalizedString)
      .addProperty(contain, OBJECT)
    model.write(new FileOutputStream("trigram.rdf"))
    logger.info("TriGraM core created with [%d] triples".format(model.size))
  }

}