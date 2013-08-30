/**
 * Modeler to translate directory
 */
package modeler

import java.io.{ File, FileOutputStream }
import scalax.file.Path
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary.{ RDF, RDFS, OWL, OWL2, DC_11 => DC, DCTerms => DT }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import util.{ Logging, Version, DateTime, Hash }

import modeler.{ CimVocabulary => CIM }

/**
 * @author ShiZhan
 * translate directory structure into CIM model
 */
object Directory extends Modeler with Logging {

  override val key = "dir"

  override val usage = "Translate directory structure"

  def run(input: String, output: String) = {
    val p = Path(new File(input))

    if (p.isDirectory) {
      logger.info("creating model for directory [{}]", p.path)

      val base = p.toURI.toString
      val ns = base + "#"

      val m = ModelFactory.createDefaultModel

      m.setNsPrefix(key, ns)
      m.setNsPrefix(CimSchema.key, CIM.NS)
      m.createResource(base, OWL.Ontology)
        .addProperty(DC.date, DateTime.get, XSDdateTime)
        .addProperty(DC.description, "TriGraM Directory model", XSDstring)
        .addProperty(OWL.versionInfo, Version.get, XSDstring)
        .addProperty(OWL.imports, CIM.IMPORT("CIM_Directory"))
        .addProperty(OWL.imports, CIM.IMPORT("CIM_DataFile"))
        .addProperty(OWL.imports, CIM.IMPORT("CIM_DirectoryContainsFile"))

      def genNodeUri(p: Path) = ns + Hash.getMD5(p.path)

      def assignAttributes(p: Path) = {
        val name = p.name
        val size = if (p.size.nonEmpty) p.size.get.toString else "0"
        val lastMod = DateTime.get(p.lastModified)
        val canRead = p.canRead.toString
        val canWrite = p.canWrite.toString
        val canExecute = p.canExecute.toString

        if (p.isDirectory) {
          val dirUri = genNodeUri(p)
          val dirRes = m.createResource(dirUri, OWL2.NamedIndividual)
            .addProperty(RDF.`type`, CIM.CLASS("CIM_Directory"))
            .addProperty(CIM.PROP("Name"), name, XSDnormalizedString)
            .addProperty(CIM.PROP("FileSize"), size, XSDunsignedLong)
            .addProperty(CIM.PROP("LastModified"), lastMod, XSDdateTime)
            .addProperty(CIM.PROP("Readable"), canRead, XSDboolean)
            .addProperty(CIM.PROP("Writeable"), canWrite, XSDboolean)
            .addProperty(CIM.PROP("Executable"), canExecute, XSDboolean)
          m.createResource(dirUri + "_dcf", OWL2.NamedIndividual)
            .addProperty(RDF.`type`, CIM.CLASS("CIM_DirectoryContainsFile"))
            .addProperty(CIM.PROP("GroupComponent"), dirRes)
        } else {
          m.createResource(genNodeUri(p), OWL2.NamedIndividual)
            .addProperty(RDF.`type`, CIM.CLASS("CIM_DataFile"))
            .addProperty(CIM.PROP("Name"), name, XSDnormalizedString)
            .addProperty(CIM.PROP("FileSize"), size, XSDunsignedLong)
            .addProperty(CIM.PROP("LastModified"), lastMod, XSDdateTime)
            .addProperty(CIM.PROP("Readable"), canRead, XSDboolean)
            .addProperty(CIM.PROP("Writeable"), canWrite, XSDboolean)
            .addProperty(CIM.PROP("Executable"), canExecute, XSDboolean)
        }
      }

      assignAttributes(p)

      logger.info("reading directory ...")

      val ps = p ** "*"

      val total = ps.size
      val delta = if (total < 100) 1 else total / 100
      var progress = 0

      logger.info("[{}] objects", total)

      for (i <- ps) {
        assignAttributes(i)

        val dirRef = m.getResource(genNodeUri(i.parent.get) + "_dcf")
        val current = m.getResource(genNodeUri(i))
        val stmt = m.createStatement(dirRef, CIM.PROP("PartComponent"), current)
        m.add(stmt)

        progress += 1
        if (progress % delta == 0)
          print("translating [%2d%%]\r".format(progress * 100 / total))
      }
      println("translating [100%]")

      m.write(new FileOutputStream(output), "RDF/XML-ABBREV")

      logger.info("[{}] triples generated", m.size)
    } else {
      logger.info("[{}] is not a directory", p.name)
    }
  }

}