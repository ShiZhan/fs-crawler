/**
 * Modeler to translate directory
 */
package modeler

import scalax.file.{ Path, PathSet }
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary.{ RDF, RDFS, OWL, OWL2, DC_11 => DC, DCTerms => DT }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import util.{ Logging, Version, DateTime, Hash }

/**
 * @author ShiZhan
 * translate directory structure into TriGraM model
 */
object DIR {

  val local = Directory.key + ".owl"
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

object Directory extends Modeler with Logging {

  override val key = "Directory"

  override val usage = "Translate directory structure into TriGraM model"

  def tBox = {
    logger.info("initialize core model")

    val license = """
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

    val m = ModelFactory.createDefaultModel

    m.setNsPrefix("tgm", DIR.ns)
    m.createResource(DIR.base, OWL.Ontology)
      .addProperty(DC.date, DateTime.get, XSDdateTime)
      .addProperty(DC.description, "TriGraM core model", XSDstring)
      .addProperty(DT.license, license, XSDstring)
      .addProperty(OWL.versionInfo, Version.get, XSDstring)

    m.createResource(DIR.name.getURI, OWL.DatatypeProperty)
    m.createResource(DIR.size.getURI, OWL.DatatypeProperty)
    m.createResource(DIR.lastModified.getURI, OWL.DatatypeProperty)
    m.createResource(DIR.canRead.getURI, OWL.DatatypeProperty)
    m.createResource(DIR.canWrite.getURI, OWL.DatatypeProperty)
    m.createResource(DIR.canExecute.getURI, OWL.DatatypeProperty)
    m.createResource(DIR.isDirectory.getURI, OWL.DatatypeProperty)

    m.createResource(DIR.contain.getURI, OWL.ObjectProperty)

    m.createResource(DIR.Object.getURI, OWL.Class)
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, DIR.name)
        .addProperty(OWL2.cardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.normalizedString))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, DIR.size)
        .addProperty(OWL2.cardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.unsignedLong))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, DIR.lastModified)
        .addProperty(OWL2.cardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.dateTime))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, DIR.canRead)
        .addProperty(OWL2.cardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.xboolean))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, DIR.canWrite)
        .addProperty(OWL2.cardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.xboolean))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, DIR.canExecute)
        .addProperty(OWL2.cardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.xboolean))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, DIR.isDirectory)
        .addProperty(OWL2.cardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.xboolean))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, DIR.contain)
        .addProperty(OWL.allValuesFrom, DIR.Object))

    m.write(new java.io.FileOutputStream(DIR.local), "RDF/XML-ABBREV")

    logger.info("created [%d] triples in TBox [%s]".format(m.size, DIR.local))
  }

  def aBox(input: String, output: String) = {
    val p = Path(input)

    if (p.isDirectory) {
      logger.info("creating model for directory [%s]".format(p.path))

      val base = "http://localhost/directory/" + input
      val ns = base + "#"

      val m = ModelFactory.createDefaultModel

      m.setNsPrefix("tgm", DIR.ns)
      m.createResource(base, OWL.Ontology)
        .addProperty(DC.date, DateTime.get, XSDdateTime)
        .addProperty(DC.description, "TriGraM directory model", XSDstring)
        .addProperty(OWL.versionInfo, Version.get, XSDstring)
        .addProperty(OWL.imports, DIR.Import)

      def genNodeUri(p: Path) = ns + Hash.getMD5(p.path)

      def assignAttributes(p: Path) = {
        val pSize = if (p.size.nonEmpty) p.size.get.toString else "0"

        m.createResource(genNodeUri(p), OWL2.NamedIndividual)
          .addProperty(RDF.`type`, DIR.Object)
          .addProperty(DIR.name, p.name, XSDnormalizedString)
          .addProperty(DIR.size, pSize, XSDunsignedLong)
          .addProperty(DIR.lastModified, DateTime.get(p.lastModified), XSDdateTime)
          .addProperty(DIR.canRead, p.canRead.toString, XSDboolean)
          .addProperty(DIR.canWrite, p.canWrite.toString, XSDboolean)
          .addProperty(DIR.canExecute, p.canExecute.toString, XSDboolean)
          .addProperty(DIR.isDirectory, p.isDirectory.toString, XSDboolean)
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
          DIR.contain,
          m.getResource(genNodeUri(i))))
      }

      m.write(new java.io.FileOutputStream(output), "RDF/XML-ABBREV")

      logger.info("[%d] triples written".format(m.size))
    } else {
      logger.info("[%s] is not a directory".format(p.name))
    }
  }

}