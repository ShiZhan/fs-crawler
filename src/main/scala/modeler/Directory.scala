/**
 * Modeler to translate directory
 */
package modeler

import java.io.{ File, FileOutputStream }
import scalax.file.Path
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

object DirectoryVocabulary {

  private val uriPrefix = "https://sites.google.com/site/ontology2013/"
  private val depName = List("CIM_Base", "CIM_Properties",
    "CIM_Directory", "CIM_DataFile", "CIM_DirectoryContainsFile")
  private val depRes = depName.map {
    case name => {
      val depFN = name + ".owl"
      val depURI = uriPrefix + depFN
      val depNS = depURI + "#"
      name -> (depFN, depURI, depNS)
    }
  } toMap
  private def getFN(name: String) = depRes.getOrElse(name, ("", "", ""))._1
  private def getURI(name: String) = depRes.getOrElse(name, ("", "", ""))._2
  private def getNS(name: String) = depRes.getOrElse(name, ("", "", ""))._3

  private val model = ModelFactory.createDefaultModel

  /*
   * directory imports & concepts
   */
  val baseNS = getNS("CIM_Base")
  val importBase = model.createResource(getURI("CIM_Base"))
  val CIM_Meta_Class = model.createResource(baseNS + "CIM_Meta_Class")
  val CIM_Association = model.createResource(baseNS + "CIM_Association")

  val importDataFile = model.createResource(getURI("CIM_DataFile"))
  val importDirectory = model.createResource(getURI("CIM_Directory"))
  val importDirectoryContainsFile = model.createResource(getURI("CIM_DirectoryContainsFile"))
  val CIM_DataFile = model.createResource(
    getNS("CIM_DataFile") + "CIM_DataFile")
  val CIM_Directory = model.createResource(
    getNS("CIM_Directory") + "CIM_Directory")
  val CIM_DirectoryContainsFile = model.createResource(
    getNS("CIM_DirectoryContainsFile") + "CIM_DirectoryContainsFile")

  /*
   * directory vocabulary
   */
  val propNS = getNS("CIM_Properties")

  private val propertyList = List(
    "AvailableRequestedStates", "AvailableSpace", "BlockSize", "Caption",
    "CasePreserved", "CaseSensitive", "ClusterSize", "CodeSet", "CommunicationStatus",
    "CompressionMethod", "CreationClassName", "CreationDate", "CSCreationClassName",
    "CSName", "Description", "DetailedStatus", "ElementName", "EnabledDefault",
    "EnabledState", "EncryptionMethod", "Executable", "FileSize", "FileSystemSize",
    "FileSystemType", "FSCreationClassName", "FSName", "Generation", "GroupComponent",
    "HealthState", "InstallDate", "InstanceID", "InUseCount", "IsFixedSize",
    "LastAccessed", "LastModified", "MaxFileNameLength", "Name", "NumberOfFiles",
    "OperatingStatus", "OperationalStatus", "OtherEnabledState", "OtherPersistenceType",
    "PartComponent", "PersistenceType", "PrimaryStatus", "Readable", "ReadOnly",
    "RequestedState", "ResizeIncrement", "Root", "Status", "StatusDescriptions",
    "TimeOfLastStateChange", "TransitioningToState", "Writeable")
    .map(n => n -> model.createProperty(propNS + n)) toMap

  private val invalidProperty = model.createProperty(propNS + "invalidProperty")
  def PROP(n: String) = propertyList.getOrElse(n, invalidProperty)
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
software distributed under the License is distributed on an "AS IS" BASIS,
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

    m.createResource(DIR.contain.getURI, OWL.ObjectProperty)

    List(DIR.name, DIR.size, DIR.lastModified,
      DIR.canRead, DIR.canWrite, DIR.canExecute, DIR.isDirectory)
      .foreach(p => m.createResource(p.getURI, OWL.DatatypeProperty))

    m.createResource(DIR.Object.getURI, OWL.Class)
      .addProperty(RDFS.subClassOf, m.createResource(OWL.Restriction)
        .addProperty(OWL.onProperty, DIR.contain)
        .addProperty(OWL.allValuesFrom, DIR.Object))
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

    m.write(new FileOutputStream(DIR.local), "RDF/XML-ABBREV")

    logger.info("created [{}] triples in TBox [{}]", m.size, DIR.local)
  }

  def aBox(input: String, output: String) = {
    val p = Path(new File(input))

    if (p.isDirectory) {
      logger.info("creating model for directory [{}]", p.path)

      val base = p.toURI.toString
      val ns = base + "#"

      val m = ModelFactory.createDefaultModel

      m.setNsPrefix(key, DIR.ns)
      m.createResource(base, OWL.Ontology)
        .addProperty(DC.date, DateTime.get, XSDdateTime)
        .addProperty(DC.description, "TriGraM Directory model", XSDstring)
        .addProperty(OWL.versionInfo, Version.get, XSDstring)
        .addProperty(OWL.imports, DIR.Import)

      def genNodeUri(p: Path) = ns + Hash.getMD5(p.path)

      def assignAttributes(p: Path) = {
        val name = p.name
        val size = if (p.size.nonEmpty) p.size.get.toString else "0"
        val lastMod = DateTime.get(p.lastModified)
        val canRead = p.canRead.toString
        val canWrite = p.canWrite.toString
        val canExecute = p.canExecute.toString
        val isDirectory = p.isDirectory.toString

        m.createResource(genNodeUri(p), OWL2.NamedIndividual)
          .addProperty(RDF.`type`, DIR.Object)
          .addProperty(DIR.name, name, XSDnormalizedString)
          .addProperty(DIR.size, size, XSDunsignedLong)
          .addProperty(DIR.lastModified, lastMod, XSDdateTime)
          .addProperty(DIR.canRead, canRead, XSDboolean)
          .addProperty(DIR.canWrite, canWrite, XSDboolean)
          .addProperty(DIR.canExecute, canExecute, XSDboolean)
          .addProperty(DIR.isDirectory, isDirectory, XSDboolean)
      }

      assignAttributes(p)

      logger.info("reading directory ...")

      val ps = p ** "*"

      val total = ps.size
      val delta = if (total < 100) 1 else total / 100
      var progress = 0

      logger.info("[{}] objects", total)

      for (i <- ps) {
        assignAttributes(i)

        val dir = m.getResource(genNodeUri(i.parent.get))
        val current = m.getResource(genNodeUri(i))
        val stmt = m.createStatement(dir, DIR.contain, current)
        m.add(stmt)

        progress += 1
        if (progress % delta == 0)
          print("translating [%2d%%]\r".format(progress * 100 / total))
      }
      println("translating [100%]")

      m.write(new FileOutputStream(output), "RDF/XML-ABBREV")

      logger.info("[{}] triples generated", m.size)
    } else {
      logger.info("[{}] is not a directory", p.name)
    }
  }

}