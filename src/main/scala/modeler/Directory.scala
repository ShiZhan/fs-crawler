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
 * translate directory structure into CIM model
 */
object DIR {

  private val model = ModelFactory.createDefaultModel

  /*
   * directory imports & concepts
   */
  private val uriPrefix = "https://sites.google.com/site/ontology2013/"

  private val depRes = List(
    "CIM_Directory", "CIM_DataFile", "CIM_DirectoryContainsFile")
    .map {
      case name => {
        val depURI = uriPrefix + name + ".owl"
        val depImport = model.createResource(depURI)
        val depClass = model.createResource(depURI + "#" + name)
        name -> (depImport, depClass)
      }
    } toMap

  private val unknown = model.createResource

  def IMPORT(name: String) = depRes.getOrElse(name, (unknown, unknown))._1
  def CLASS(name: String) = depRes.getOrElse(name, (unknown, unknown))._2

  /*
   * directory vocabulary
   */
  val propNS = uriPrefix + "CIM_Properties.owl#"

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
    logger.info("initialize core model [obsolete]")
  }

  def aBox(input: String, output: String) = {
    val p = Path(new File(input))

    if (p.isDirectory) {
      logger.info("creating model for directory [{}]", p.path)

      val base = p.toURI.toString
      val ns = base + "#"

      val m = ModelFactory.createDefaultModel

      m.setNsPrefix("prop", DIR.propNS)
      m.createResource(base, OWL.Ontology)
        .addProperty(DC.date, DateTime.get, XSDdateTime)
        .addProperty(DC.description, "TriGraM Directory model", XSDstring)
        .addProperty(OWL.versionInfo, Version.get, XSDstring)
        .addProperty(OWL.imports, DIR.IMPORT("CIM_Directory"))
        .addProperty(OWL.imports, DIR.IMPORT("CIM_DataFile"))
        .addProperty(OWL.imports, DIR.IMPORT("CIM_DirectoryContainsFile"))

      def genNodeUri(p: Path) = ns + Hash.getMD5(p.path)

      def assignAttributes(p: Path) = {
        val name = p.name
        val size = if (p.size.nonEmpty) p.size.get.toString else "0"
        val lastMod = DateTime.get(p.lastModified)
        val canRead = p.canRead.toString
        val canWrite = p.canWrite.toString
        val canExecute = p.canExecute.toString

        if (p.isDirectory) {
          val dirUri = genNodeUri(p)
          val dirRes = m.createResource(dirUri, OWL2.NamedIndividual)
            .addProperty(RDF.`type`, DIR.CLASS("CIM_Directory"))
            .addProperty(DIR.PROP("Name"), name, XSDnormalizedString)
            .addProperty(DIR.PROP("FileSize"), size, XSDunsignedLong)
            .addProperty(DIR.PROP("LastModified"), lastMod, XSDdateTime)
            .addProperty(DIR.PROP("Readable"), canRead, XSDboolean)
            .addProperty(DIR.PROP("Writeable"), canWrite, XSDboolean)
            .addProperty(DIR.PROP("Executable"), canExecute, XSDboolean)
          m.createResource(dirUri + "_dcf", OWL2.NamedIndividual)
            .addProperty(RDF.`type`, DIR.CLASS("CIM_DirectoryContainsFile"))
            .addProperty(DIR.PROP("GroupComponent"), dirRes)
        } else {
          m.createResource(genNodeUri(p), OWL2.NamedIndividual)
            .addProperty(RDF.`type`, DIR.CLASS("CIM_DataFile"))
            .addProperty(DIR.PROP("Name"), name, XSDnormalizedString)
            .addProperty(DIR.PROP("FileSize"), size, XSDunsignedLong)
            .addProperty(DIR.PROP("LastModified"), lastMod, XSDdateTime)
            .addProperty(DIR.PROP("Readable"), canRead, XSDboolean)
            .addProperty(DIR.PROP("Writeable"), canWrite, XSDboolean)
            .addProperty(DIR.PROP("Executable"), canExecute, XSDboolean)
        }
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

        val dirRef = m.getResource(genNodeUri(i.parent.get) + "_dcf")
        val current = m.getResource(genNodeUri(i))
        val stmt = m.createStatement(dirRef, DIR.PROP("PartComponent"), current)
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