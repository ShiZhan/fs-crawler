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
object SeZ {

  val local = "tgm" + SevenZip.key + ".owl"
  val base = "https://sites.google.com/site/ontology2013/" + local
  val ns = base + "#"

  private val model = ModelFactory.createDefaultModel
  val Import = model.createResource(base)

  val ArchiveFile = model.createResource(ns + "ArchiveFile")
  val ArchiveEntry = model.createResource(ns + "ArchiveEntry")

  val hasEntry = model.createProperty(ns + "hasEntry")
  val path = model.createProperty(ns + "path")
  val archiveType = model.createProperty(ns + "archiveType")
  val isDirectory = model.createProperty(ns + "isDirectory")
  val size = model.createProperty(ns + "size")
  val packedSize = model.createProperty(ns + "packedSize")
  val lastModified = model.createProperty(ns + "lastModified")
  val created = model.createProperty(ns + "created")
  val accessed = model.createProperty(ns + "accessed")
  val attributes = model.createProperty(ns + "attributes")
  val isEncrypted = model.createProperty(ns + "isEncrypted")
  val comment = model.createProperty(ns + "comment") // annotation?
  val crc = model.createProperty(ns + "crc")
  val method = model.createProperty(ns + "method")
  val hostOS = model.createProperty(ns + "hostOS")
  val version = model.createProperty(ns + "version")
}

object SevenZip extends Modeler with Logging {

  override val key = "sez"

  override val usage =
    "Translate 7-Zip archive (7z.exe l -slt foo.{7z|zip|rar} > list.txt)"

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

    m.setNsPrefix(key, SeZ.ns)
    m.createResource(SeZ.base, OWL.Ontology)
      .addProperty(DC.date, DateTime.get, XSDdateTime)
      .addProperty(DC.description, "TriGraM 7-Zip Archive model", XSDstring)
      .addProperty(DT.license, license, XSDstring)
      .addProperty(OWL.versionInfo, Version.get, XSDstring)

    m.createResource(SeZ.hasEntry.getURI, OWL.ObjectProperty)
    List(SeZ.path, SeZ.archiveType, SeZ.isDirectory, SeZ.size, SeZ.packedSize,
      SeZ.lastModified, SeZ.created, SeZ.accessed, SeZ.attributes,
      SeZ.isEncrypted, SeZ.comment, SeZ.crc, SeZ.method,
      SeZ.hostOS, SeZ.version)
      .foreach(p => m.createResource(p.getURI, OWL.DatatypeProperty))

    m.createResource(SeZ.ArchiveFile.getURI, OWL.Class)
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, SeZ.hasEntry)
        .addProperty(OWL.allValuesFrom, SeZ.ArchiveEntry))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, SeZ.path)
        .addProperty(OWL2.cardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.normalizedString))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, SeZ.archiveType)
        .addProperty(OWL2.cardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.dateTime))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, SeZ.size)
        .addProperty(OWL2.cardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.unsignedLong))

    m.createResource(SeZ.ArchiveEntry.getURI, OWL.Class)
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, SeZ.path)
        .addProperty(OWL2.cardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.normalizedString))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, SeZ.isDirectory)
        .addProperty(OWL2.cardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.xboolean))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, SeZ.size)
        .addProperty(OWL2.cardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.unsignedLong))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, SeZ.packedSize)
        .addProperty(OWL2.cardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.unsignedLong))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, SeZ.lastModified)
        .addProperty(OWL2.maxCardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.dateTime))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, SeZ.created)
        .addProperty(OWL2.maxCardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.dateTime))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, SeZ.accessed)
        .addProperty(OWL2.maxCardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.dateTime))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, SeZ.attributes)
        .addProperty(OWL2.maxCardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.normalizedString))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, SeZ.isEncrypted)
        .addProperty(OWL2.maxCardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.xboolean))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, SeZ.comment)
        .addProperty(OWL2.maxCardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.xstring))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, SeZ.crc)
        .addProperty(OWL2.maxCardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.hexBinary))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, SeZ.method)
        .addProperty(OWL2.maxCardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.normalizedString))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, SeZ.hostOS)
        .addProperty(OWL2.maxCardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.normalizedString))
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, SeZ.version)
        .addProperty(OWL2.maxCardinality, "1", XSDnonNegativeInteger)
        .addProperty(OWL2.onDataRange, XSD.unsignedInt))

    m.write(new java.io.FileOutputStream(SeZ.local), "RDF/XML-ABBREV")

    logger.info("created [{}] triples in TBox [{}]", m.size, SeZ.local)
  }

  def aBox(input: String, output: String) = {
    val f = new File(input)
    if (!f.exists)
      logger.error("input source does not exist")
    else if (!f.isFile)
      logger.error("input source is not file")
    else {
      logger.info("Model 7-Zip file content [{}]", f.getAbsolutePath)

      val bFIS = new BufferedInputStream(new FileInputStream(f))
      val base = f.toURI.toString
      val ns = base + "#"

      val m = ModelFactory.createDefaultModel

      m.setNsPrefix(key, SeZ.ns)
      m.createResource(base, OWL.Ontology)
        .addProperty(DC.date, DateTime.get, XSDdateTime)
        .addProperty(DC.description, "TriGraM 7-Zip Archive model", XSDstring)
        .addProperty(OWL.versionInfo, Version.get, XSDstring)
        .addProperty(OWL.imports, SeZ.Import)

      // read text

      // translate to individual

      m.write(new FileOutputStream(output), "RDF/XML-ABBREV")

      logger.info("[{}] triples generated", m.size)
    }
  }

}