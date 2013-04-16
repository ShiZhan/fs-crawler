/**
 * Modeler to translate directory
 */
package modeler

import scalax.file.{ Path, PathSet }
import com.hp.hpl.jena.rdf.model.{ ModelFactory, Model }
import com.hp.hpl.jena.vocabulary.{ RDF, RDFS, OWL, OWL2, DC_11 => DC, DCTerms => DT }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import util.{ Logging, Version, DateTime, Hash }

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
      .addProperty(DC.date, DateTime.get, XSDdateTime)
      .addProperty(DC.description, "TriGraM core model", XSDstring)
      .addProperty(DT.license, license, XSDstring)
      .addProperty(OWL.versionInfo, Version.get, XSDstring)

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
      logger.info("creating model for directory [%s]".format(p.path))

      val base = "http://localhost/directory/" + n
      val ns = base + "#"

      m.setNsPrefix("tgm", TGM.ns)
      m.createResource(base, OWL.Ontology)
        .addProperty(DC.date, DateTime.get, XSDdateTime)
        .addProperty(DC.description, "TriGraM directory model", XSDstring)
        .addProperty(OWL.versionInfo, Version.get, XSDstring)
        .addProperty(OWL.imports, TGM.Import)

      def genNodeUri(p: Path) = ns + Hash.getMD5(p.path)

      def assignAttributes(p: Path) = {
        val pSize = if (p.size.nonEmpty) p.size.get.toString else "0"

        m.createResource(genNodeUri(p), OWL2.NamedIndividual)
          .addProperty(RDF.`type`, TGM.Object)
          .addProperty(TGM.name, p.name, XSDnormalizedString)
          .addProperty(TGM.size, pSize, XSDunsignedLong)
          .addProperty(TGM.lastModified, DateTime.get(p.lastModified), XSDdateTime)
          .addProperty(TGM.canRead, p.canRead.toString, XSDboolean)
          .addProperty(TGM.canWrite, p.canWrite.toString, XSDboolean)
          .addProperty(TGM.canExecute, p.canExecute.toString, XSDboolean)
      }

      assignAttributes(p)

      val ps = p.***
      for (i <- ps) {
        logger.info("[%s/%s] in [%s]: %d|%d|%s|%s|%s".format(
          i.name, i.path, i.parent.get.name, if (i.size.nonEmpty) i.size.get else 0,
          i.lastModified, i.canRead, i.canWrite, i.canExecute))

        assignAttributes(i)
        m.add(m.createStatement(
          m.getResource(genNodeUri(i.parent.get)),
          TGM.contain,
          m.getResource(genNodeUri(i))))
      }
    } else {
      logger.info("[%s] is not a directory".format(p.name))
    }

    m
  }

}