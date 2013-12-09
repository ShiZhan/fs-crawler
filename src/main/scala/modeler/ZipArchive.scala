/**
 * Modeler for compressed resources
 */
package modeler

import java.io.{ File, FileOutputStream }
import org.apache.commons.compress.archivers.zip.{ ZipFile, ZipArchiveEntry }
import org.apache.commons.codec.digest.DigestUtils.md5Hex
import com.hp.hpl.jena.rdf.model.{ ModelFactory, Model, Resource }
import com.hp.hpl.jena.vocabulary.{ OWL, OWL2, DC_11 => DC, RDF }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import util.{ Logging, Version, DateTime, URI }
import modeler.{ CimVocabulary => CIM }

/**
 * @author ShiZhan
 * translate zip-like file (zip|jar|apk ...) contents into semantic model
 * can be used with Directory modeler to reveal the detail of a file system
 */
case class ZipModel(base: String, nsPrefix: String) {
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

case class ZipFileModel(f: File) {
  def addTo(m: Model) = {
    val path = f.getAbsolutePath
    val uri = URI.fromFile(f)
    val size = f.length.toString
    val modi = DateTime.get(f.lastModified)
    val zipFile = m.createResource(uri, OWL2.NamedIndividual)
      .addProperty(RDF.`type`, CIM.CLASS("CIM_DataFile"))
      .addProperty(CIM.PROP("Name"), path, XSDnormalizedString)
      .addProperty(CIM.PROP("FileSize"), size, XSDunsignedLong)
      .addProperty(CIM.PROP("LastModified"), modi, XSDdateTime)
    zipFile.addProperty(RDF.`type`, CIM.CLASS("CIM_Component"))
      .addProperty(CIM.PROP("GroupComponent"), zipFile)
  }
}

case class ZipEntryModel(e: ZipArchiveEntry, checksum: String) {
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

object ZipArchive extends Modeler with Logging {

  override val key = "zip"

  override val usage = "<zip archive> => [triples],\n\t\tnow support [zip, jar, apk]."

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

  private def readZip(file: File) = {
    try {
      val zf = new ZipFile(file)
      val entries = zf.getEntries
      Iterator.continually {
        if (entries.hasMoreElements) entries.nextElement else null
      }.takeWhile(null !=).map { e =>
        var md5 = ""
        if (!e.isDirectory) {
          val is = zf.getInputStream(e)
          md5 = md5Hex(is)
          is.close
        }
        ZipEntryModel(e, md5)
      }
    } catch {
      case e: Exception => e.printStackTrace; Iterator[ZipEntryModel]()
    }
  }

  private def translate(f: File) = {
    val zipPath = f.getAbsolutePath

    logger.info("Model zipped file [{}]", zipPath)

    val m = ZipModel(URI.fromHost, key).create
    val zipFile = ZipFileModel(f).addTo(m)
    readZip(f) foreach { _.addTo(m, zipFile, zipPath + '/') }

    val output = zipPath + "-model.owl"
    m.write(new FileOutputStream(output), "RDF/XML-ABBREV")

    logger.info("[{}] triples generated in [{}]", m.size, output)
  }

}