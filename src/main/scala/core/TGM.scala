/**
 * TriGraM core model
 */
package core

import java.io.FileOutputStream
import java.util.Calendar
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import com.hp.hpl.jena.ontology.OntModelSpec
import com.hp.hpl.jena.vocabulary.{ RDF, RDFS, OWL, OWL2, XSD => JenaXSD, DC_11 => DC }
import util.{ Logging, Version }

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
  val hexBinary = JenaXSD.hexBinary
}

/**
 * @author ShiZhan
 * TriGraM core model vocabulary, concept and statement
 */
object TGM extends Logging {
  logger.info("initialize core model")

  val base = "https://sites.google.com/site/ontology2013/trigram.owl"
  val uri = base + "#"
  private val objectUri = uri + "object"
  private val containUri = uri + "contain"
  private val nameUri = uri + "name"
  private val model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF)
  private val defaultModelName = "trigram.rdf"

  model.setNsPrefix("tgm", uri)
  model.createOntology(base)
    .addProperty(DC.date, Calendar.getInstance.getTime.toLocaleString)
    .addProperty(OWL.versionInfo, Version.getVersion)
  val contain = model.createObjectProperty(containUri)
  val name = model.createDatatypeProperty(nameUri)
  val Object = model.createClass(objectUri)
  Object.addSuperClass(model.createResource(OWL.Restriction)
    .addProperty(OWL.onProperty, name)
    .addProperty(OWL2.cardinality, "1", XSDnonNegativeInteger)
    .addProperty(OWL2.onDataRange, XSD.normalizedString))
  Object.addSuperClass(model.createResource(OWL.Restriction)
    .addProperty(OWL.onProperty, contain)
    .addProperty(OWL.allValuesFrom, Object))

  def writeCoreModel = {
    model.write(new FileOutputStream(defaultModelName))

    logger.info("TriGraM core created with [%d] triples".format(model.getBaseModel.size))
  }

}