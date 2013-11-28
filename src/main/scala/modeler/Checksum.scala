/**
 * Modeler to translate file content into (checksum, position) model
 */
package modeler

import scala.io.Source
import java.io.{ File, FileOutputStream }
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary.{ RDF, OWL, DC_11 => DC, DCTerms => DT }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import org.apache.commons.codec.digest.DigestUtils
import util.{ Logging, Version, DateTime, URI }

import modeler.{ CimVocabulary => CIM }

/**
 * @author ShiZhan
 * Translate content characteristics of 'virtually' any data source into
 * structural model item(checksum, size, path, [item, ...]) for easy comparison
 */
object Checksum extends Modeler with Logging {
  override val key = "chk"

  override val usage = "<file> <chunk size: Bytes> => [structural checksum group]"

  def run(options: Array[String]) = {
    options.toList match {
      case fileName :: chunkSizeStr :: Nil => {
        val f = new File(fileName)
        if (!f.exists)
          logger.error("input source does not exist")
        else if (!f.isFile)
          logger.error("input source is not file")
        else {
          val chunkSize = getInt(chunkSizeStr) getOrElse 65536
          translate(f, chunkSize)
        }
      }
      case _ => logger.error("parameter error: [{}]", options)
    }
  }

  private def getInt(s: String): Option[Int] = {
    try {
      Some(s.toInt)
    } catch {
      case e: Exception => None
    }
  }

  private def translate(f: File, chunkSize: Int) = {
    logger.info("Model file [{}]", f.getAbsolutePath)

    val fileSize = f.length
    val fileBS = Source.fromFile(f)
    val chunks = fileBS.grouped(chunkSize)
    val MD5List = chunks.map { c => DigestUtils.md5Hex(c.mkString) }.toList
    fileBS.close

    val base = URI.fromHost
    val ns = base + "CHK#"
    val m = ModelFactory.createOntologyModel
    m.setNsPrefix(key, ns)
    m.setNsPrefix(CimSchema.key, CIM.NS)
    m.createOntology(base)
      .addProperty(DC.date, DateTime.get, XSDdateTime)
      .addProperty(DC.description, "TriGraM file chunk checksum model", XSDstring)
      .addProperty(OWL.versionInfo, Version.get, XSDstring)
      .addProperty(OWL.imports, CIM.IMPORT("CIM_Directory"))
      .addProperty(OWL.imports, CIM.IMPORT("CIM_DataFile"))
      .addProperty(OWL.imports, CIM.IMPORT("CIM_DirectoryContainsFile"))
      .addProperty(OWL.imports, CIM.IMPORT("CIM_FileSpecification"))

    val uri = URI.fromFile(f)
    val path = f.getAbsolutePath
    val size = fileSize.toString
    val modi = DateTime.get(f.lastModified)
    val file = m.createIndividual(uri, CIM.CLASS("CIM_Directory"))
      .addProperty(CIM.PROP("Name"), path, XSDnormalizedString)
      .addProperty(CIM.PROP("FileSize"), size, XSDunsignedLong)
      .addProperty(CIM.PROP("LastModified"), modi, XSDdateTime)
      .addProperty(RDF.`type`, CIM.CLASS("CIM_DirectoryContainsFile"))
    file.addProperty(CIM.PROP("GroupComponent"), file)

    for ((md5, i) <- MD5List.zipWithIndex) {
      val chunkURI = uri + '.' + i
      val chunkName = path + '.' + i
      val realSize = if (i == MD5List.length - 1) fileSize % chunkSize else chunkSize
      val chunk = m.createIndividual(chunkURI, CIM.CLASS("CIM_DataFile"))
        .addProperty(CIM.PROP("Name"), chunkName, XSDnormalizedString)
        .addProperty(CIM.PROP("FileSize"), realSize.toString, XSDunsignedLong)
        .addProperty(CIM.PROP("LastModified"), modi, XSDdateTime)
        .addProperty(RDF.`type`, CIM.CLASS("CIM_FileSpecification"))
        .addProperty(CIM.PROP("MD5Checksum"), md5, XSDnormalizedString)
        .addProperty(CIM.PROP("FileName"), chunkName, XSDnormalizedString)
      file.addProperty(CIM.PROP("PartComponent"), chunk)
    }

    val output = f.getAbsolutePath + "-chk.owl"
    m.write(new FileOutputStream(output), "RDF/XML-ABBREV")

    logger.info("[{}] triples generated in [{}]", m.size, output)
  }
}