/**
 * Adapter to translate directory
 */
package modeler

import java.util.Calendar
import scalax.file.{ Path, PathSet }
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary.{ RDF, RDFS, OWL, OWL2, DC_11 => DC, DCTerms => DT }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import util.{ Logging, Version }

/**
 * @author ShiZhan
 * translate directory structure into TriGraM model
 */
object Directory extends Modeler with Logging {

  private val license = """
Copyright 2013 Shi.Zhan.
Licensed under the Apache License, Version 2.0 (the &quot;License&quot;);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing
permissions and limitations under the License. 
"""

  def usage = "Translate directory structure into TriGraM model"

  def core = {
    logger.info("initialize core model")

    val m = ModelFactory.createDefaultModel

    m.setNsPrefix("tgm", TGM.ns)
    m.createResource(TGM.base, OWL.Ontology)
      .addProperty(DC.date, Calendar.getInstance.getTime.toLocaleString, XSDdateTime)
      .addProperty(DC.description, "TriGraM core model", XSDstring)
      .addProperty(DT.license, license, XSDstring)
      .addProperty(OWL.versionInfo, Version.getVersion, XSDstring)

    m.createResource(TGM.name.getURI, OWL.DatatypeProperty)
    m.createResource(TGM.size.getURI, OWL.DatatypeProperty)
    m.createResource(TGM.lastModified.getURI, OWL.DatatypeProperty)
    m.createResource(TGM.canRead.getURI, OWL.DatatypeProperty)
    m.createResource(TGM.canWrite.getURI, OWL.DatatypeProperty)
    m.createResource(TGM.canExecute.getURI, OWL.DatatypeProperty)

    m.createResource(TGM.contain.getURI, OWL.ObjectProperty)

    m.createResource(TGM.Object.getURI, OWL.Class)
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, TGM.name)
        .addProperty(OWL2.cardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.normalizedString))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, TGM.size)
        .addProperty(OWL2.cardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.unsignedLong))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, TGM.lastModified)
        .addProperty(OWL2.cardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.dateTime))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, TGM.canRead)
        .addProperty(OWL2.cardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.xboolean))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, TGM.canWrite)
        .addProperty(OWL2.cardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.xboolean))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, TGM.canExecute)
        .addProperty(OWL2.cardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.xboolean))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, TGM.contain)
        .addProperty(OWL.allValuesFrom, TGM.Object))

    logger.info("created [%d] triples in core model".format(m.size))

    m
  }

  def translate(n: String) = {
    val p = Path(n)

    val m = ModelFactory.createDefaultModel

    if (p.isDirectory) {
      logger.info("creating model for directory [%s]".format(p.name))

      val base = "http://localhost/directory/" + n
      val ns = base + "#"

      m.setNsPrefix("tgm", TGM.ns)
      m.createResource(base, OWL.Ontology)
        .addProperty(DC.date, Calendar.getInstance.getTime.toLocaleString, XSDdateTime)
        .addProperty(DC.description, "TriGraM directory model", XSDstring)
        .addProperty(OWL.versionInfo, Version.getVersion, XSDstring)
        .addProperty(OWL.imports, TGM.Import)

      m.createResource(ns + "root", OWL2.NamedIndividual)
        .addProperty(RDF.`type`, TGM.Object)
        .addProperty(TGM.name, n, XSDnormalizedString)

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