/**
 * Modeler for compressed resources
 */
package modeler

import java.io.{
  File,
  FileInputStream,
  FileOutputStream,
  InputStreamReader,
  BufferedInputStream
}
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary.{
  RDF,
  RDFS,
  OWL,
  OWL2,
  DC_11 => DC,
  DCTerms => DT
}
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import util.{ Logging, Version, DateTime, Hash }

/**
 * @author ShiZhan
 * translate archive file contents into semantic model
 * can be used with Directory modeler to reveal the detail of a file system
 */
object ARC {

  val local = "tgm" + Archive.key + ".owl"
  val base = "https://sites.google.com/site/ontology2013/" + local
  val ns = base + "#"

  private val model = ModelFactory.createDefaultModel
  val Import = model.createResource(base)

  val ArchiveFile = model.createResource(ns + "ArchiveFile")
  val ArchiveEntry = model.createResource(ns + "ArchiveEntry")

  val hasEntry = model.createProperty(ns + "hasEntry")
  val name = model.createProperty(ns + "name")
  val size = model.createProperty(ns + "size")
  val lastModified = model.createProperty(ns + "lastModified")
  val isDirectory = model.createProperty(ns + "isDirectory")

}

object Archive extends Modeler with Logging {

  override val key = "arc"

  override val usage = "Translate archive file contents (ar|cpio|jar|tar|zip)"

  def tBox = {
    logger.info("initialize core model")

    val license = """
Copyright 2013 Shi.Zhan.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing
permissions and limitations under the License. 
"""

    val m = ModelFactory.createDefaultModel

    m.setNsPrefix(key, ARC.ns)
    m.createResource(ARC.base, OWL.Ontology)
      .addProperty(DC.date, DateTime.get, XSDdateTime)
      .addProperty(DC.description, "TriGraM Archive model", XSDstring)
      .addProperty(DT.license, license, XSDstring)
      .addProperty(OWL.versionInfo, Version.get, XSDstring)

    m.createResource(ARC.hasEntry.getURI, OWL.ObjectProperty)
    List(ARC.name, ARC.size, ARC.lastModified, ARC.isDirectory)
      .foreach(p => m.createResource(p.getURI, OWL.DatatypeProperty))

    m.createResource(ARC.ArchiveFile.getURI, OWL.Class)
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, ARC.hasEntry)
        .addProperty(OWL.allValuesFrom, ARC.ArchiveEntry))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, ARC.name)
        .addProperty(OWL2.cardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.normalizedString))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, ARC.size)
        .addProperty(OWL2.cardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.unsignedLong))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, ARC.lastModified)
        .addProperty(OWL2.cardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.dateTime))

    m.createResource(ARC.ArchiveEntry.getURI, OWL.Class)
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, ARC.name)
        .addProperty(OWL2.cardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.normalizedString))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, ARC.size)
        .addProperty(OWL2.cardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.unsignedLong))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, ARC.lastModified)
        .addProperty(OWL2.cardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.dateTime))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, ARC.isDirectory)
        .addProperty(OWL2.cardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.xboolean))

    m.write(new java.io.FileOutputStream(ARC.local), "RDF/XML-ABBREV")

    logger.info("created [{}] triples in TBox [{}]", m.size, ARC.local)
  }

  def aBox(input: String, output: String) = {
    val f = new File(input)
    if (!f.exists)
      logger.error("input source does not exist")
    else if (!f.isFile)
      logger.error("input source is not file")
    else {
      logger.info("Model zipped file [{}]", f.getAbsolutePath)

      val base = f.toURI.toString
      val ns = base + "#"

      val m = ModelFactory.createDefaultModel

      m.setNsPrefix(key, ARC.ns)
      m.createResource(base, OWL.Ontology)
        .addProperty(DC.date, DateTime.get, XSDdateTime)
        .addProperty(DC.description, "TriGraM Archive model", XSDstring)
        .addProperty(OWL.versionInfo, Version.get, XSDstring)
        .addProperty(OWL.imports, ARC.Import)

      val bFIS = new BufferedInputStream(new FileInputStream(f))
      val aSF = new ArchiveStreamFactory
      val aIS = aSF.createArchiveInputStream(bFIS)
      val iAIS = Iterator.continually { aIS.getNextEntry }.takeWhile(_ != null)

      val archiveFile = m.createResource(ns + aIS, OWL2.NamedIndividual)
        .addProperty(RDF.`type`, ARC.ArchiveFile)
        .addProperty(ARC.name, f.getAbsolutePath, XSDnormalizedString)
        .addProperty(ARC.size, f.length.toString, XSDunsignedLong)
        .addProperty(ARC.lastModified, DateTime.get(f.lastModified), XSDdateTime)

      for (e <- iAIS) {
        val name = e.getName
        val uri = ns + Hash.getMD5(name)
        val size = e.getSize.toString
        val lastM = DateTime.get(e.getLastModifiedDate)
        val isDir = e.isDirectory.toString
        val entry = m.createResource(uri, OWL2.NamedIndividual)
          .addProperty(RDF.`type`, ARC.ArchiveEntry)
          .addProperty(ARC.name, name, XSDnormalizedString)
          .addProperty(ARC.size, size, XSDunsignedLong)
          .addProperty(ARC.lastModified, lastM, XSDdateTime)
          .addProperty(ARC.isDirectory, isDir, XSDboolean)

        archiveFile.addProperty(ARC.hasEntry, entry)
      }

      m.write(new FileOutputStream(output), "RDF/XML-ABBREV")

      logger.info("[{}] triples generated", m.size)
    }
  }

}