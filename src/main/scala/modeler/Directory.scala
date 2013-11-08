/**
 * Modeler to translate directory
 */
package modeler

import java.io.{ File, FileOutputStream }
import scalax.file.Path
import com.hp.hpl.jena.rdf.model.{ ModelFactory, Model }
import com.hp.hpl.jena.vocabulary.{ RDF, RDFS, OWL, OWL2, DC_11 => DC, DCTerms => DT }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import util.{ Logging, Version, DateTime, Platform, URI }

import modeler.{ CimVocabulary => CIM }

/**
 * @author ShiZhan
 * translate directory structure into CIM model
 */
object Directory extends Modeler with Logging {

  override val key = "dir"

  override val usage = "<directory> => [triples]"

  private def assignAttributes(m: Model, p: Path) = {
    val name = p.toAbsolute.path
    val size = if (p.size.nonEmpty) p.size.get.toString else "0"
    val lastMod = DateTime.get(p.lastModified)
    val canRead = p.canRead.toString
    val canWrite = p.canWrite.toString
    val canExecute = p.canExecute.toString

    if (p.isDirectory) {
      val dirUri = URI.fromPath(p)
      val dirRes = m.createResource(dirUri, OWL2.NamedIndividual)
        .addProperty(RDF.`type`, CIM.CLASS("CIM_Directory"))
        .addProperty(CIM.PROP("Name"), name, XSDnormalizedString)
        .addProperty(CIM.PROP("FileSize"), size, XSDunsignedLong)
        .addProperty(CIM.PROP("LastModified"), lastMod, XSDdateTime)
        .addProperty(CIM.PROP("Readable"), canRead, XSDboolean)
        .addProperty(CIM.PROP("Writeable"), canWrite, XSDboolean)
        .addProperty(CIM.PROP("Executable"), canExecute, XSDboolean)
      val dirRef = m.createResource(dirUri + ".dcf", OWL2.NamedIndividual)
        .addProperty(RDF.`type`, CIM.CLASS("CIM_DirectoryContainsFile"))
        .addProperty(CIM.PROP("GroupComponent"), dirRes)

      for (subPath <- p * "*") {
        val subPathRes = m.getResource(URI.fromPath(subPath))
        dirRef.addProperty(CIM.PROP("PartComponent"), subPathRes)
      }

    } else {
      m.createResource(URI.fromPath(p), OWL2.NamedIndividual)
        .addProperty(RDF.`type`, CIM.CLASS("CIM_DataFile"))
        .addProperty(CIM.PROP("Name"), name, XSDnormalizedString)
        .addProperty(CIM.PROP("FileSize"), size, XSDunsignedLong)
        .addProperty(CIM.PROP("LastModified"), lastMod, XSDdateTime)
        .addProperty(CIM.PROP("Readable"), canRead, XSDboolean)
        .addProperty(CIM.PROP("Writeable"), canWrite, XSDboolean)
        .addProperty(CIM.PROP("Executable"), canExecute, XSDboolean)
    }
  }

  def run(options: Array[String]) = {
    val input = options(0)
    val p = Path(new File(input))

    if (p.isDirectory) {
      logger.info("creating model for directory [{}]", p.toAbsolute.path)

      val m = ModelFactory.createDefaultModel

      val base = URI.fromHost

      m.setNsPrefix(key, base + "#")
      m.setNsPrefix(CimSchema.key, CIM.NS)
      m.createResource(base, OWL.Ontology)
        .addProperty(DC.date, DateTime.get, XSDdateTime)
        .addProperty(DC.description, "TriGraM Directory model", XSDstring)
        .addProperty(OWL.versionInfo, Version.get, XSDstring)
        .addProperty(OWL.imports, CIM.IMPORT("CIM_Directory"))
        .addProperty(OWL.imports, CIM.IMPORT("CIM_DataFile"))
        .addProperty(OWL.imports, CIM.IMPORT("CIM_DirectoryContainsFile"))

      assignAttributes(m, p)

      logger.info("reading directory ...")

      val ps = p ** "*"

      val total = ps.size
      val delta = if (total < 100) 1 else total / 100
      var progress = 0

      logger.info("[{}] objects", total)

      for (i <- ps) {
        assignAttributes(m, i)

        progress += 1
        if (progress % delta == 0)
          print("translating [%2d%%]\r".format(progress * 100 / total))
      }
      println("translating [100%]")

      val output = if (options.length > 1) options(1) else input + "-model.owl"
      m.write(new FileOutputStream(output), "RDF/XML-ABBREV")

      logger.info("[{}] triples generated in [{}]", m.size, output)
    } else {
      logger.info("[{}] is not a directory", p.name)
    }
  }
}