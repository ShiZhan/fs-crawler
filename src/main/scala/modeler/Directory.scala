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

  val local = "tgm" + Directory.key + ".owl"
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

  override val key = "dir"

  override val usage = "Translate directory structure"

  def tBox = {
    logger.info("initialize core model")

    val license = """
Copyright 2013 Shi.Zhan.
Licensed under the Apache License, Version 2.0 (the "License");
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

    m.setNsPrefix(key, DIR.ns)
    m.createResource(DIR.base, OWL.Ontology)
      .addProperty(DC.date, DateTime.get, XSDdateTime)
      .addProperty(DC.description, "TriGraM Directory model", XSDstring)
      .addProperty(DT.license, license, XSDstring)
      .addProperty(OWL.versionInfo, Version.get, XSDstring)

    List(DIR.name, DIR.size, DIR.lastModified,
      DIR.canRead, DIR.canWrite, DIR.canExecute, DIR.isDirectory)
      .foreach(p => m.createResource(p.getURI, OWL.DatatypeProperty))

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
    val p = Path(new java.io.File(input))

    if (p.isDirectory) {
      logger.info("creating model for directory [%s]".format(p.path))

      val base = p.toURI.toString
      val ns = base + "#"

      val m = ModelFactory.createDefaultModel

      m.setNsPrefix("dir", DIR.ns)
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

      logger.info("reading directory ...")

      val ps = p ** "*"

      val total = ps.size
      val delta = if (total < 100) 1 else total / 100
      var progress = 0

      logger.info("[%d] objects".format(total))

      for (i <- ps) {
        assignAttributes(i)
        m.add(m.createStatement(
          m.getResource(genNodeUri(i.parent.get)),
          DIR.contain,
          m.getResource(genNodeUri(i))))

        progress += 1
        if (progress % delta == 0)
          print("translating [%2d%%]\r".format(progress * 100 / total))
      }
      println("translating [100%]")

      m.write(new java.io.FileOutputStream(output), "RDF/XML-ABBREV")

      logger.info("[%d] triples generated".format(m.size))
    } else {
      logger.info("[%s] is not a directory".format(p.name))
    }
  }

}