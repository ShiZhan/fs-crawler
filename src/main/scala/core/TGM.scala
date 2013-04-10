/**
 * TriGraM core model
 */
package core

import com.hp.hpl.jena.rdf.model.impl.{ ResourceImpl, PropertyImpl }
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary.{ RDF, RDFS, OWL, XSD => JenaXSD }
import java.io.FileOutputStream
import util.Logging

/*
 * Patch for mapping Jena RDF type to Scala RDF TYPE,
 * avoid the conflict with internal term.
 */
import com.hp.hpl.jena.vocabulary.RDF.{ `type` => TYPE }
/*
 * Patch for mapping Jena XSD var to Scala XSD val
 */
object XSD {
  val integer = JenaXSD.integer
  val unsignedLong = JenaXSD.unsignedLong
  val nonNegativeInteger = JenaXSD.nonNegativeInteger
  val string = JenaXSD.xstring
  val normalizedString = JenaXSD.normalizedString
  val dateTime = JenaXSD.dateTime
}

/**
 * @author ShiZhan
 * TriGraM core model vocabulary, concept and statement
 */
object TGM extends Logging {
  val base = "https://sites.google.com/site/ontology2013/trigram.owl"
  val uri = base + "#"
  val OBJECT = new ResourceImpl(uri + "object")
  val contain = new PropertyImpl(uri + "contain")
  val name = new PropertyImpl(uri + "name")

  def createCore = {
    logger.info("initialize core model")

    val model = ModelFactory.createDefaultModel
    model.setNsPrefix("tgm", uri)
    model.createResource(base)
      .addProperty(TYPE, OWL.Ontology)
    model.createProperty(contain.getURI)
      .addProperty(TYPE, OWL.ObjectProperty)
    model.createProperty(name.getURI)
      .addProperty(TYPE, OWL.DatatypeProperty)
    model.createResource(OBJECT.getURI)
      .addProperty(TYPE, OWL.Class)
      .addProperty(RDFS.subClassOf, model.createResource
        .addProperty(TYPE, OWL.Restriction)
        .addProperty(OWL.onProperty, name)
        .addProperty(OWL.allValuesFrom, XSD.normalizedString))
      .addProperty(RDFS.subClassOf, model.createResource
        .addProperty(TYPE, OWL.Restriction)
        .addProperty(OWL.onProperty, contain)
        .addProperty(OWL.allValuesFrom, OBJECT))

    model.write(new FileOutputStream("trigram.rdf"))
    logger.info("TriGraM core created with [%d] triples".format(model.size))
  }

}