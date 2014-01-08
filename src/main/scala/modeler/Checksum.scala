/**
 * Modeler to translate directory content checksum into tree-structural model
 */
package modeler

import java.io.{ File, FileInputStream, FileOutputStream, BufferedInputStream }
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.ontology.OntModel
import com.hp.hpl.jena.vocabulary.{ RDF, OWL, DC_11 => DC, DCTerms => DT }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import org.apache.commons.codec.digest.DigestUtils.md5Hex
import util.DigestUtilsAddon.md5HexChunk
import util.{ Logging, Version, DateTime, URI }
import modeler.{ CimVocabulary => CIM }

/**
 * @author ShiZhan
 * Translate content characteristics of recognized data source into
 * tree-structural model [(checksum, id(path)), [(, ), ...]] for comparison
 */
object FileCheckers {
  case class md5Tuple(md5sum: String, path: String, size: Long) {
    def addTo(model: OntModel) = {
      model.createIndividual(URI.fromString(path), CIM.CLASS("CIM_DataFile"))
        .addProperty(CIM.PROP("Name"), path, XSDnormalizedString)
        .addProperty(CIM.PROP("FileSize"), size.toString, XSDunsignedLong)
        .addProperty(RDF.`type`, CIM.CLASS("CIM_FileSpecification"))
        .addProperty(CIM.PROP("MD5Checksum"), md5sum, XSDnormalizedString)
        .addProperty(CIM.PROP("FileName"), path, XSDnormalizedString)
    }
  }

  def fileMD5(file: File) = {
    val fIS = new BufferedInputStream(new FileInputStream(file))
    val md5 = md5Hex(fIS)
    fIS.close
    md5Tuple(md5, file.getAbsolutePath, file.length)
  }

  def chunkMD5(file: File, chunkSize: Long) = {
    val fileSize = file.length
    if (fileSize > chunkSize) {
      val indexOfLastChunk = fileSize / chunkSize
      val sizeOfLastChunk = fileSize % chunkSize
      val fileAbsolutePath = file.getAbsolutePath
      val fileInputStream = new BufferedInputStream(new FileInputStream(file))
      val md5Array = (0 to indexOfLastChunk.toInt).map { i =>
        val md5 = md5HexChunk(fileInputStream, chunkSize)
        val path = fileAbsolutePath + "." + i
        val size = if (i == indexOfLastChunk) sizeOfLastChunk else chunkSize
        md5Tuple(md5, path, size)
      }.toArray
      fileInputStream.close
      md5Array
    } else
      Array[md5Tuple]()
  }
}

case class ChecksumModel(base: String, nsPrefix: String) {
  val ns = base + "CHK#"
  val m = ModelFactory.createOntologyModel
  m.setNsPrefix(nsPrefix, ns)
  m.setNsPrefix(CimSchema.key, CIM.NS)
  m.createOntology(base)
    .addProperty(DC.date, DateTime.get, XSDdateTime)
    .addProperty(DC.description, "TriGraM checksum model", XSDstring)
    .addProperty(OWL.versionInfo, Version.get, XSDstring)
    .addProperty(OWL.imports, CIM.IMPORT("CIM_DataFile"))
    .addProperty(OWL.imports, CIM.IMPORT("CIM_OrderedComponent"))
    .addProperty(OWL.imports, CIM.IMPORT("CIM_FileSpecification"))
  def create = m
}

case class FileChecksumModel(file: File) {
  def addTo(model: OntModel) = FileCheckers.fileMD5(file) addTo model
}

case class ChunkChecksumModel(file: File, chunkSize: Long) {
  def addTo(model: OntModel) = {
    val chunked = FileCheckers.fileMD5(file) addTo model
    val cMD5s = FileCheckers.chunkMD5(file, chunkSize)
    if (!cMD5s.isEmpty) {
      for ((cMD5, index) <- cMD5s.zipWithIndex) {
        val chunk = cMD5 addTo model
        chunk.addProperty(RDF.`type`, CIM.CLASS("CIM_OrderedComponent"))
          .addProperty(CIM.PROP("GroupComponent"), chunked)
          .addProperty(CIM.PROP("PartComponent"), chunk)
          .addProperty(CIM.PROP("AssignedSequence"), index.toString, XSDunsignedInt)
      }
    }
  }
}

object Checksum extends Modeler with Logging {
  override val key = "chk"

  override val usage = "<source> <output.owl> [<chunk size: Bytes>] => [output.owl]"

  def run(options: Array[String]) = {
    options.toList match {
      case fileName :: output :: Nil => {
        val source = new File(fileName)
        if (!source.exists) logger.error("input source does not exist")
        else if (source.isFile)
          translateFile(source, output)
        else
          translateDir(source, output)
      }
      case fileName :: output :: chunkSizeStr :: Nil => {
        val source = new File(fileName)
        val chunkSize = chunkSizeStr.toLong
        if (!source.exists) logger.error("input source does not exist")
        else if (source.isFile)
          translateFile(source, output, chunkSize)
        else
          translateDir(source, output, chunkSize)
      }
      case _ => logger.error("parameter error: [{}]", options)
    }
  }

  private def listAllFiles(dir: File): Array[File] = {
    assert(dir.isDirectory)

    val list = dir.listFiles
    list ++ list.filter(_.isDirectory).flatMap(listAllFiles)
  }

  private def translateFile(file: File, output: String) = {
    logger.info("Model source [{}]", file.getAbsolutePath)

    val m = ChecksumModel(URI.fromHost, key).create
    FileChecksumModel(file) addTo m

    m.write(new FileOutputStream(output), "RDF/XML-ABBREV")

    logger.info("[{}] triples generated in [{}]", m.size, output)
  }

  private def translateFile(file: File, output: String, chunkSize: Long) = {
    logger.info("Model source [{}]", file.getAbsolutePath)

    val m = ChecksumModel(URI.fromHost, key).create
    ChunkChecksumModel(file, chunkSize) addTo m

    m.write(new FileOutputStream(output), "RDF/XML-ABBREV")

    logger.info("[{}] triples generated in [{}]", m.size, output)
  }

  private def translateDir(dir: File, output: String) = {
    logger.info("Model source [{}]", dir.getAbsolutePath)

    val m = ChecksumModel(URI.fromHost, key).create
    listAllFiles(dir).filter(_.isFile).foreach(FileChecksumModel(_) addTo m)

    m.write(new FileOutputStream(output), "RDF/XML-ABBREV")

    logger.info("[{}] triples generated in [{}]", m.size, output)
  }

  private def translateDir(dir: File, output: String, chunkSize: Long) = {
    logger.info("Model source [{}]", dir.getAbsolutePath)

    val m = ChecksumModel(URI.fromHost, key).create
    listAllFiles(dir).filter(_.isFile).foreach(ChunkChecksumModel(_, chunkSize) addTo m)

    m.write(new FileOutputStream(output), "RDF/XML-ABBREV")

    logger.info("[{}] triples generated in [{}]", m.size, output)
  }
}