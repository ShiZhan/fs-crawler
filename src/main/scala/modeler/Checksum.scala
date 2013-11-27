/**
 * Modeler to translate file content into (checksum, position) model
 */
package modeler

import java.io.{ File, FileInputStream, FileOutputStream }
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary.{ RDF, RDFS, OWL, OWL2, DC_11 => DC, DCTerms => DT }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import org.apache.commons.codec.digest.DigestUtils
import util.{ Logging, Version, DateTime, Hash, URI }

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
    val stream = new FileInputStream(f)
    val total = fileSize / chunkSize
    val remain = fileSize % chunkSize
    val fileMD5 = DigestUtils.md5Hex(stream)
    println(fileMD5)

    stream.close

    val base = URI.fromHost
    val ns = base + "CHK#"
    val m = ModelFactory.createOntologyModel
    m.setNsPrefix(key, ns)
    m.setNsPrefix(CimSchema.key, CIM.NS)
    m.createOntology(base)
      .addProperty(DC.date, DateTime.get, XSDdateTime)
      .addProperty(DC.description, "TriGraM file chunk checksum model", XSDstring)
      .addProperty(OWL.versionInfo, Version.get, XSDstring)
      .addProperty(OWL.imports, CIM.IMPORT("CIM_DataFile"))

    val chkURI = URI.fromFile(f)

    val chkPath = f.getAbsolutePath
    val chkSize = f.length.toString
    val chkModi = DateTime.get(f.lastModified)
    val chkFile = m.createIndividual(chkURI, CIM.CLASS("CIM_Directory"))
      .addProperty(CIM.PROP("Name"), chkPath, XSDnormalizedString)
      .addProperty(CIM.PROP("FileSize"), chkSize, XSDunsignedLong)
      .addProperty(CIM.PROP("LastModified"), chkModi, XSDdateTime)

    val output = f.getAbsolutePath + "-chk.owl"
    m.write(new FileOutputStream(output), "RDF/XML-ABBREV")

    logger.info("[{}] triples generated in [{}]", m.size, output)
  }
}