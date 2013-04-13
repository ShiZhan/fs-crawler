/**
 * Adapter to translate directory
 */
package modeler

import java.util.Calendar
import scalax.file.{ Path, PathSet }
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.ontology.OntModelSpec
import com.hp.hpl.jena.vocabulary.{ RDF, RDFS, OWL, OWL2, DC_11 => DC }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import util.{ Logging, Version }
/**
 * @author ShiZhan
 * translate directory structure into TriGraM model
 */
object Directory extends Modeler with Logging {

  private val help = "Translate directory structure into TriGraM model"

  def usage = { help }

  def core = {
    logger.info("initialize core model")

    val m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF)

    m.setNsPrefix("tgm", TGM.ns)
    m.createOntology(TGM.base)
      .addProperty(DC.date, Calendar.getInstance.getTime.toLocaleString, XSDdateTime)
      .addProperty(DC.description, "TriGraM directory model", XSDstring)
      .addProperty(OWL.versionInfo, Version.getVersion, XSDstring)

    m.createObjectProperty(TGM.contain.getURI)
    m.createDatatypeProperty(TGM.name.getURI)
    m.createClass(TGM.Object.getURI)
      .addProperty(RDFS.subClassOf, OWL.Restriction)
        .addProperty(OWL.onProperty, TGM.name)
        .addProperty(OWL2.cardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.normalizedString)
      .addProperty(RDFS.subClassOf, OWL.Restriction)
        .addProperty(OWL.onProperty, TGM.contain)
        .addProperty(OWL.allValuesFrom, TGM.Object)

    m
  }

  def translate(n: String) = {
    val base = "http://localhost/directory/" + n
    val ns = base + "#"
    val m = ModelFactory.createOntologyModel

    m.setNsPrefix("tgm", TGM.ns)
    val ont = m.createOntology(base)
    ont.addProperty(DC.date, Calendar.getInstance.getTime.toLocaleString, XSDdateTime)
    ont.addProperty(DC.description, "TriGraM directory model", XSDstring)
    ont.addProperty(OWL.versionInfo, Version.getVersion, XSDstring)
    ont.addImport(TGM.Import)

    val p = Path(n)

    m.createResource(ns + "root", OWL2.NamedIndividual)
      .addProperty(RDF.`type`, TGM.Object)
      .addProperty(TGM.name, n, XSDnormalizedString)

    if (p.isDirectory) {
      logger.info("creating model for directory [%s]".format(p.name))

      val ps = p.***
      for (i <- ps) {
        logger.info("[%s] in [%s]: %d|%d|%s|%s|%s".format(
          i.name, i.parent.get.name, if (i.size.nonEmpty) i.size.get else 0,
          i.lastModified, i.canRead, i.canWrite, i.canExecute))
      }
    } else {
      logger.info("[%s] is not a directory".format(p.name))
    }

    m
  }

}