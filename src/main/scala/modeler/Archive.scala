/**
 * Modeler for compressed resources
 */
package modeler

import java.io.{ File, FileOutputStream }
import org.apache.commons.compress.archivers.ArchiveEntry
import com.hp.hpl.jena.rdf.model.{ ModelFactory, Model, Resource }
import com.hp.hpl.jena.vocabulary.{ OWL, OWL2, DC_11 => DC, RDF }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import util.{ Logging, Version, DateTime, URI }
import modeler.{ CimVocabulary => CIM }

/**
 * @author ShiZhan
 * translate archive file contents into semantic model
 * can be used with Directory modeler to reveal the detail of a file system
 */
object ArchiveCheckers {
  import java.io.{ File, FileInputStream }
  import org.apache.commons.compress.archivers.zip.ZipFile
  import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
  import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
  import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
  import org.apache.commons.codec.digest.DigestUtils.md5Hex
  import util.DigestUtilsAddon.md5HexChunk

  private def checkZip(file: File) = {
    try {
      val zf = new ZipFile(file)
      val entries = zf.getEntries
      val files = Iterator.continually {
        if (entries.hasMoreElements) entries.nextElement else null
      }.takeWhile(null !=).filter(!_.isDirectory)
      files map { e =>
        val is = zf.getInputStream(e)
        val md5 = md5Hex(is)
        is.close
        ArcEntryModel(e, md5)
      }
    } catch {
      case e: Exception => e.printStackTrace; Iterator[ArcEntryModel]()
    }
  }

  private def checkGzip(file: File) = {
    val fis = new FileInputStream(file)
    val gzis = new GzipCompressorInputStream(fis)
    val tis = new TarArchiveInputStream(gzis)
    try {
      val files = Iterator.continually(tis.getNextTarEntry)
        .takeWhile(null !=).filter(_.isFile)
      files map { e =>
        val size = e.getSize
        val md5 = md5HexChunk(tis, size)
        ArcEntryModel(e, md5)
      }
    } catch {
      case e: Exception => e.printStackTrace; Iterator[ArcEntryModel]()
    }
  }

  private def checkBz2(file: File) = {
    val fis = new FileInputStream(file)
    val bzis = new BZip2CompressorInputStream(fis)
    val tis = new TarArchiveInputStream(bzis)
    try {
      val files = Iterator.continually(tis.getNextTarEntry)
        .takeWhile(null !=).filter(_.isFile)
      files map { e =>
        val size = e.getSize
        val md5 = md5HexChunk(tis, size)
        ArcEntryModel(e, md5)
      }
    } catch {
      case e: Exception => e.printStackTrace; Iterator[ArcEntryModel]()
    }
  }

  /*
   * @TODO: handler for 7zip
   */
  private def check7Zip(file: File) = {
    Iterator[ArcEntryModel]()
  }

  type arcChecker = (File => Iterator[ArcEntryModel])
  private val arcCheckers = Map[String, arcChecker](
    "zip" -> checkZip,
    "jar" -> checkZip,
    "war" -> checkZip,
    "apk" -> checkZip,
    "epub" -> checkZip,
    "odt" -> checkZip,
    "ods" -> checkZip,
    "odp" -> checkZip,
    "odg" -> checkZip,
    "docx" -> checkZip,
    "xlsx" -> checkZip,
    "pptx" -> checkZip,
    "tgz" -> checkGzip,
    "gz" -> checkGzip,
    "bz2" -> checkBz2,
    "7z" -> check7Zip)
  private def defaultChecker(f: File) = Iterator[ArcEntryModel]()
  val knownExt = arcCheckers map { case (k, c) => k } toSet
  def checkArc(file: File) = {
    val fileNameExtension = file.getName.split('.').last
    arcCheckers.getOrElse(fileNameExtension, defaultChecker _)(file)
  }
}

case class ArcModel(base: String, nsPrefix: String) {
  val m = ModelFactory.createDefaultModel
  m.setNsPrefix(nsPrefix, base + "#")
  m.setNsPrefix(CimSchema.key, CIM.NS)
  m.createResource(base, OWL.Ontology)
    .addProperty(DC.date, DateTime.get, XSDdateTime)
    .addProperty(DC.description, "TriGraM Archive model", XSDstring)
    .addProperty(OWL.versionInfo, Version.get, XSDstring)
    .addProperty(OWL.imports, CIM.IMPORT("CIM_Directory"))
    .addProperty(OWL.imports, CIM.IMPORT("CIM_DataFile"))
    .addProperty(OWL.imports, CIM.IMPORT("CIM_Component"))
    .addProperty(OWL.imports, CIM.IMPORT("CIM_FileSpecification"))
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
    arcFile.addProperty(RDF.`type`, CIM.CLASS("CIM_Component"))
      .addProperty(CIM.PROP("GroupComponent"), arcFile)
  }
}

case class ArcEntryModel(e: ArchiveEntry, checksum: String) {
  def addTo(m: Model, zip: Resource, prefix: String) = {
    val path = e.getName
    val uri = URI.fromString(prefix + path)
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

object Archive extends Modeler with Logging {

  override val key = "arc"

  override val usage = "<archive> => [triples],\n\t\tnow support [zip, tar.gz, tar.bz2]."

  def run(options: Array[String]) = {
    options.toList match {
      case fileName :: Nil => {
        val f = new File(fileName)
        if (!f.exists)
          logger.error("input source does not exist")
        else if (!f.isFile)
          logger.error("input source is not file")
        else
          translate(f)
      }
      case _ => logger.error("parameter error: [{}]", options)
    }
  }

  private def translate(f: File) = {
    val arcPath = f.getAbsolutePath

    logger.info("Model archive file [{}]", arcPath)

    val m = ArcModel(URI.fromHost, key).create
    val arcFile = ArcFileModel(f).addTo(m)
    ArchiveCheckers.checkArc(f) foreach { _.addTo(m, arcFile, arcPath + '/') }

    val output = arcPath + "-model.owl"
    m.write(new FileOutputStream(output), "RDF/XML-ABBREV")

    logger.info("[{}] triples generated in [{}]", m.size, output)
  }

}