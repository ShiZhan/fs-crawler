/**
 * Modeler to translate directory content checksum into tree-structural model
 */
package modeler

import scala.io.Source
import java.io.{ File, FileOutputStream }
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.ontology.OntModel
import com.hp.hpl.jena.vocabulary.{ RDF, OWL, DC_11 => DC, DCTerms => DT }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._

import util.{ Logging, Version, DateTime, URI, Hash }
import modeler.{ CimVocabulary => CIM }

/**
 * @author ShiZhan
 * Translate content characteristics of recognized data source into
 * tree-structural model [(checksum, id(path)), [(, ), ...]] for comparison
 */
case class md5Tuple(md5sum: String, path: String, size: Long) {
  def -->(model: OntModel) = {
    model.createIndividual(URI.fromString(path), CIM.CLASS("CIM_DataFile"))
      .addProperty(CIM.PROP("Name"), path, XSDnormalizedString)
      .addProperty(CIM.PROP("FileSize"), size.toString, XSDunsignedLong)
      .addProperty(RDF.`type`, CIM.CLASS("CIM_FileSpecification"))
      .addProperty(CIM.PROP("MD5Checksum"), md5sum, XSDnormalizedString)
      .addProperty(CIM.PROP("FileName"), path, XSDnormalizedString)
  }
}

object Checksum extends Modeler with Logging {
  override val key = "chk"

  override val usage = "<source> [<chunk size: Bytes>] => [structural checksum tree]"

  def run(options: Array[String]) = {
    if (options.length == 1) {
      val source = new File(options(0))
      if (!source.exists)
        println("input source does not exist")
      else if (source.isFile)
        translateFile(source)
      else
        translateDir(source)
    } else if (options.length == 2) {
      val source = new File(options(0))
      val chunkSize = getInt(options(1)) getOrElse 65536
      if (!source.exists)
        println("input source does not exist")
      else if (source.isFile)
        translateFile(source, chunkSize)
      else
        translateDir(source, chunkSize)
    } else
      logger.error("parameter error: [{}]", options)
  }

  private def getInt(s: String): Option[Int] = {
    try { Some(s.toInt) }
    catch { case e: Exception => None }
  }

  private def fileMD5(file: File) = {
    val fileBuffer = Source.fromFile(file, "ISO-8859-1")
    val fileBytes = fileBuffer.map(_.toByte)
    val md5 = Hash.md5sum(fileBytes)
    fileBuffer.close
    md5Tuple(md5, file.getAbsolutePath, file.length)
  }

  private def chunkMD5(file: File, chunkSize: Int) = {
    val size = file.length
    if (size > chunkSize) {
      val fileBuffer = Source.fromFile(file, "ISO-8859-1")
      val fileBytes = fileBuffer.map(_.toByte)
      val chunks = fileBytes.grouped(chunkSize).map(_.iterator)
      val md5Array = chunks.map(Hash.md5sum).toArray
      fileBuffer.close
      val lastChunk = size / chunkSize
      val lastSize = size % chunkSize
      val path = file.getAbsolutePath
      md5Array.zipWithIndex.map {
        case (m, i) =>
          md5Tuple(m, path + "." + i, if (i == lastChunk) lastSize else chunkSize)
      }
    } else
      Array[md5Tuple]()
  }

  private def collect(dir: File, chunkSize: Int) = {

    def checkDir(d: File): Array[(md5Tuple, Array[md5Tuple])] = {
      val (files, dirs) = d.listFiles.partition(_.isFile)
      val md5Files = files.map { f => (fileMD5(f), chunkMD5(f, chunkSize)) }
      md5Files ++ dirs.flatMap(checkDir)
    }

    checkDir(dir)
  }

  private def collect(dir: File) = {

    def checkDir(d: File): Array[md5Tuple] = {
      val (files, dirs) = d.listFiles.partition(_.isFile)
      val md5Files = files.map { fileMD5 }
      md5Files ++ dirs.flatMap(checkDir)
    }

    checkDir(dir)
  }

  private def translateFile(file: File) = {
    logger.info("Model source [{}]", file.getAbsolutePath)

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
      .addProperty(OWL.imports, CIM.IMPORT("CIM_FileSpecification"))

    fileMD5(file) --> m

    val output = file.getAbsolutePath + "checksums.owl"
    m.write(new FileOutputStream(output), "RDF/XML-ABBREV")

    logger.info("[{}] triples generated in [{}]", m.size, output)
  }

  private def translateFile(file: File, chunkSize: Int) = {
    logger.info("Model source [{}]", file.getAbsolutePath)

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

    val chunked = fileMD5(file) --> m
    val cMD5s = chunkMD5(file, chunkSize)
    if (!cMD5s.isEmpty) {
      chunked.addProperty(RDF.`type`, CIM.CLASS("CIM_Component"))
        .addProperty(CIM.PROP("GroupComponent"), chunked)
      for (cMD5 <- cMD5s) {
        val chunk = cMD5 --> m
        chunked.addProperty(CIM.PROP("PartComponent"), chunk)
      }
    }

    val output = file.getAbsolutePath + chunkSize + "checksums.owl"
    m.write(new FileOutputStream(output), "RDF/XML-ABBREV")

    logger.info("[{}] triples generated in [{}]", m.size, output)
  }

  private def translateDir(dir: File) = {
    logger.info("Model source [{}]", dir.getAbsolutePath)

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
      .addProperty(OWL.imports, CIM.IMPORT("CIM_FileSpecification"))

    collect(dir) foreach { _ --> m }

    val output = dir.getAbsolutePath + "checksums.owl"
    m.write(new FileOutputStream(output), "RDF/XML-ABBREV")

    logger.info("[{}] triples generated in [{}]", m.size, output)
  }

  private def translateDir(dir: File, chunkSize: Int) = {
    logger.info("Model source [{}]", dir.getAbsolutePath)

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

    for ((fMD5, cMD5s) <- collect(dir, chunkSize)) {
      val chunked = fMD5 --> m
      if (!cMD5s.isEmpty) {
        chunked.addProperty(RDF.`type`, CIM.CLASS("CIM_Component"))
          .addProperty(CIM.PROP("GroupComponent"), chunked)
        for (cMD5 <- cMD5s) {
          val chunk = cMD5 --> m
          chunked.addProperty(CIM.PROP("PartComponent"), chunk)
        }
      }
    }

    val output = dir.getAbsolutePath + chunkSize +"checksums.owl"
    m.write(new FileOutputStream(output), "RDF/XML-ABBREV")

    logger.info("[{}] triples generated in [{}]", m.size, output)
  }
}