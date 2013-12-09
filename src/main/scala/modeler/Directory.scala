/**
 * Modeler to translate directory
 */
package modeler

import java.io.{ File, FileOutputStream }
import com.hp.hpl.jena.rdf.model.{ ModelFactory, Model }
import com.hp.hpl.jena.vocabulary.{ RDF, RDFS, OWL, OWL2, DC_11 => DC, DCTerms => DT }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import util.{ Logging, Version, DateTime, Platform, URI }
import modeler.{ CimVocabulary => CIM }

/**
 * @author ShiZhan
 * translate directory structure into CIM model
 */
case class DirectoryModel(base: String, prefix: String) {
  val m = ModelFactory.createDefaultModel
  m.setNsPrefix(prefix, base + "#")
  m.setNsPrefix(CimSchema.key, CIM.NS)
  m.createResource(base, OWL.Ontology)
    .addProperty(DC.date, DateTime.get, XSDdateTime)
    .addProperty(DC.description, "TriGraM Directory model", XSDstring)
    .addProperty(OWL.versionInfo, Version.get, XSDstring)
    .addProperty(OWL.imports, CIM.IMPORT("CIM_Directory"))
    .addProperty(OWL.imports, CIM.IMPORT("CIM_DataFile"))
    .addProperty(OWL.imports, CIM.IMPORT("CIM_DirectoryContainsFile"))
  def create = m
}

case class FileModel(f: File) {
  val name = f.getAbsolutePath
  val size = f.length.toString
  val lastMod = DateTime.get(f.lastModified)
  val canRead = f.canRead.toString
  val canWrite = f.canWrite.toString
  val canExecute = f.canExecute.toString
  val isDirectory = f.isDirectory
  val uri = URI.fromFile(f)
  def addTo(m: Model) = {
    val res = m.createResource(uri, OWL2.NamedIndividual)
      .addProperty(CIM.PROP("Name"), name, XSDnormalizedString)
      .addProperty(CIM.PROP("FileSize"), size, XSDunsignedLong)
      .addProperty(CIM.PROP("LastModified"), lastMod, XSDdateTime)
      .addProperty(CIM.PROP("Readable"), canRead, XSDboolean)
      .addProperty(CIM.PROP("Writeable"), canWrite, XSDboolean)
      .addProperty(CIM.PROP("Executable"), canExecute, XSDboolean)
    if (f.isDirectory) {
      res.addProperty(RDF.`type`, CIM.CLASS("CIM_Directory"))
        .addProperty(RDF.`type`, CIM.CLASS("CIM_DirectoryContainsFile"))
      f.listFiles foreach { sFile =>
        res.addProperty(CIM.PROP("PartComponent"), m.getResource(URI.fromFile(sFile)))
      }
      res.addProperty(CIM.PROP("GroupComponent"), res)
    } else {
      res.addProperty(RDF.`type`, CIM.CLASS("CIM_DataFile"))
    }
  }
}

object Directory extends Modeler with Logging {

  override val key = "dir"

  override val usage = "<directory> [<output>] => [triples]"

  def listAllFiles(f: File): Array[File] = {
    val floor = f.listFiles
    floor ++ floor.filter(_.isDirectory).flatMap(listAllFiles)
  }

  def run(options: Array[String]) = {
    val f = new File(options(0))
    val input = f.getAbsolutePath
    val output = if (options.length > 1) options(1) else input + "-model.owl"

    if (f.isDirectory) {
      logger.info("creating model for directory [{}]", input)

      val m = DirectoryModel(URI.fromHost, key).create

      FileModel(f).addTo(m)

      logger.info("reading directory ...")

      val files = listAllFiles(f).zipWithIndex
      val total = files.size
      val delta = if (total < 100) 1 else total / 100

      logger.info("[{}] files found in [{}]", total, input)

      files foreach {
        case (file, i) =>
          FileModel(file).addTo(m)
          if (i % delta == 0) print("translating [%2d%%]\r".format(i * 100 / total))
      }
      println("translating [100%]")

      m.write(new FileOutputStream(output), "RDF/XML-ABBREV")

      logger.info("[{}] triples generated in [{}]", m.size, output)
    } else {
      logger.info("[{}] is not a directory", input)
    }
  }
}