/**
 * Modeler to translate file content into (checksum, position) model
 */
package modeler

import scala.io.Source
import java.io.{ File, FileOutputStream }
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary.{ RDF, OWL, DC_11 => DC, DCTerms => DT }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import util.{ Logging, Version, DateTime, URI, Hash }

import modeler.{ CimVocabulary => CIM }

/**
 * @author ShiZhan
 * Translate content characteristics of recognized data source into
 * structural model [(checksum, id(path)), [(, ), ...]] for comparison
 */
object Checksum extends Modeler with Logging {
  override val key = "chk"

  override val usage = "<dir> <chunk size: Bytes> => [structural checksum tree]"

  def run(options: Array[String]) = {
    options.toList match {
      case dirName :: chunkSizeStr :: Nil => {
        val dir = new File(dirName)
        if (!dir.exists)
          logger.error("input source does not exist")
        else if (dir.isFile)
          logger.error("input source is not directory")
        else {
          val chunkSize = getInt(chunkSizeStr) getOrElse 65536
          translate(dir, chunkSize)
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

  private def fileMD5(file: File) = {
    val fileBuffer = Source.fromFile(file, "ISO-8859-1")
    val fileBytes = fileBuffer.map(_.toByte)
    val md5sum = Hash.md5sum(fileBytes)
    fileBuffer.close
    md5sum
  }

  private def chunkMD5(file: File, chunkSize: Int) = {
    val fileBuffer = Source.fromFile(file, "ISO-8859-1")
    val chunks = fileBuffer.grouped(chunkSize).map(_.map(_.toByte).iterator)
    val md5sumList = chunks.map { Hash.md5sum }.toArray
    fileBuffer.close
    md5sumList
  }

  type md5Tuple = (String, String, Long)
  private def collect(dir: File, chunkSize: Int) = {

    def checkDir(d: File): Array[(md5Tuple, Array[md5Tuple])] = {
      val (files, dirs) = d.listFiles.partition(_.isFile)
      val md5Files = files.map { f =>
        val path = f.getAbsolutePath
        val size = f.length
        val md5File = fileMD5(f)
        val md5Chunks =
          if (size > chunkSize) {
            val lastChunk = size / chunkSize
            val lastSize = size % chunkSize
            val list = chunkMD5(f, chunkSize).zipWithIndex
            list.map { case (m, i) =>
              val cLength = if (i == lastChunk) lastSize else chunkSize
              (m, path + "." + i, cLength)
            }
          } else
            Array[md5Tuple]()
        ((md5File, path, size), md5Chunks)
      }
      md5Files ++ dirs.flatMap(checkDir)
    }

    checkDir(dir)
  }

  private def translate(f: File, chunkSize: Int) = {
    logger.info("Model directory [{}]", f.getAbsolutePath)

    val md5Tree = collect(f, chunkSize)

    val base = URI.fromHost
    val ns = base + "CHK#"
    val m = ModelFactory.createOntologyModel
    m.setNsPrefix(key, ns)
    m.setNsPrefix(CimSchema.key, CIM.NS)
    m.createOntology(base)
      .addProperty(DC.date, DateTime.get, XSDdateTime)
      .addProperty(DC.description, "TriGraM checksum model", XSDstring)
      .addProperty(OWL.versionInfo, Version.get, XSDstring)
      .addProperty(OWL.imports, CIM.IMPORT("CIM_DataFile"))
      .addProperty(OWL.imports, CIM.IMPORT("CIM_Component"))
      .addProperty(OWL.imports, CIM.IMPORT("CIM_FileSpecification"))

    def addMD5sum(md5tuple: md5Tuple) = {
      val (md5, path, size) = md5tuple
      m.createIndividual(URI.fromString(path), CIM.CLASS("CIM_DataFile"))
        .addProperty(CIM.PROP("Name"), path, XSDnormalizedString)
        .addProperty(CIM.PROP("FileSize"), size.toString, XSDunsignedLong)
        .addProperty(RDF.`type`, CIM.CLASS("CIM_FileSpecification"))
        .addProperty(CIM.PROP("MD5Checksum"), md5, XSDnormalizedString)
        .addProperty(CIM.PROP("FileName"), path, XSDnormalizedString)
    }

    for ((fMD5, cMD5s) <- md5Tree) {
      if (cMD5s.isEmpty) {
        addMD5sum(fMD5)
      } else {
        val chunked = addMD5sum(fMD5)
          .addProperty(RDF.`type`, CIM.CLASS("CIM_Component"))
        chunked.addProperty(CIM.PROP("GroupComponent"), chunked)
        for (cMD5 <- cMD5s) {
          val chunk = addMD5sum(cMD5)
          chunked.addProperty(CIM.PROP("PartComponent"), chunk)
        }
      }
    }

    val output = f.getAbsolutePath + "checksums.owl"
    m.write(new FileOutputStream(output), "RDF/XML-ABBREV")

    logger.info("[{}] triples generated in [{}]", m.size, output)
  }
}