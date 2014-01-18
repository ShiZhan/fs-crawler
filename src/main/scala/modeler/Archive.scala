/**
 * Modeler for compressed files
 */
package modeler

/**
 * @author ShiZhan
 * translate archive file contents into semantic model
 */
object ArchiveModels {
  import java.io.File
  import com.hp.hpl.jena.rdf.model.{ ModelFactory, Model, Resource }
  import com.hp.hpl.jena.vocabulary.{ OWL, OWL2, DC_11 => DC, RDF }
  import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
  import common.ArchiveEx.ArcEntryChecksum
  import common.URI
  import helper.{ Version, DateTime }
  import cim.{ Vocabulary => CIM }

  case class ArcModel(base: String, nsPrefix: String) {
    val m = ModelFactory.createDefaultModel
    m.setNsPrefix(nsPrefix, base + "#")
    m.setNsPrefix(CIM.NS_PREFIX, CIM.NS)
    m.createResource(base, OWL.Ontology)
      .addProperty(DC.date, DateTime.get, XSDdateTime)
      .addProperty(DC.description, "TriGraM Archive model", XSDstring)
      .addProperty(OWL.versionInfo, Version.get, XSDstring)
    def create = m
  }

  case class ArcFileModel(f: File) {
    def addTo(m: Model) = {
      val path = f.getAbsolutePath
      val uri = URI.fromFile(f)
      val size = f.length.toString
      val modi = DateTime.get(f.lastModified)
      val arcFile = m.createResource(uri, OWL2.NamedIndividual)
        .addProperty(RDF.`type`, CIM.CLASS("CIM_DataFile"))
        .addProperty(CIM.PROP("Name"), path, XSDnormalizedString)
        .addProperty(CIM.PROP("FileSize"), size, XSDunsignedLong)
        .addProperty(CIM.PROP("LastModified"), modi, XSDdateTime)
      arcFile.addProperty(RDF.`type`, CIM.CLASS("CIM_ConcreteComponent"))
        .addProperty(CIM.PROP("GroupComponent"), arcFile)
    }
  }

  implicit class ArcEntryModel(archiveEntryChecksum: ArcEntryChecksum) {
    val ArcEntryChecksum(e, arcivePath, checksum) = archiveEntryChecksum
    def addTo(m: Model, zip: Resource) = {
      val path = e.getName
      val uri = URI.fromString(arcivePath + '/' + path)
      val size = e.getSize.toString
      val modi = DateTime.get(e.getLastModifiedDate)
      val cimClass = CIM.CLASS(if (e.isDirectory) "CIM_Directory" else "CIM_DataFile")
      val entry = m.createResource(uri, OWL2.NamedIndividual)
        .addProperty(RDF.`type`, cimClass)
        .addProperty(CIM.PROP("Name"), path, XSDnormalizedString)
        .addProperty(CIM.PROP("FileSize"), size, XSDunsignedLong)
        .addProperty(CIM.PROP("LastModified"), modi, XSDdateTime)
      if (!e.isDirectory) {
        entry.addProperty(RDF.`type`, CIM.CLASS("CIM_FileSpecification"))
          .addProperty(CIM.PROP("MD5Checksum"), checksum, XSDnormalizedString)
          .addProperty(CIM.PROP("FileName"), path, XSDnormalizedString)
      }
      zip.addProperty(CIM.PROP("PartComponent"), entry)
    }
  }
}

object Archive extends Modeler with helper.Logging {
  import java.io.File
  import ArchiveModels._
  import common.ArchiveEx._
  import common.FileEx.FileOps
  import common.ModelEx.ModelOps
  import common.URI

  val key = "arc"

  val usage = "[source] [output.ow] => output.owl, support 'zip, gzip, bzip, 7z'."

  val tbox =
    Seq("CIM_Directory", "CIM_DataFile", "CIM_ConcreteComponent", "CIM_FileSpecification")

  def run(options: Array[String]) =
    options.toList match {
      case fileName :: output :: Nil => translate(new File(fileName), output)
      case _ => logger.error("parameter error: [{}]", options)
    }

  private def translate(file: File, output: String) = {
    val arcPath = file.getAbsolutePath
    logger.info("Model all supported archive file in [{}]", arcPath)
    val m = ArcModel(URI.fromHost, key).create
    file.flatten.foreach { f =>
      if (f.isFile) {
        getChecker(f) match {
          case checker: arcChecker => {
            val arc = ArcFileModel(f).addTo(m)
            for (e <- checker(f)) e.addTo(m, arc)
          }
          case _ => {}
        }
      }
    }
    m.store(output)
  }
}