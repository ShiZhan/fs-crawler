/**
 * Modeler for compressed resources
 */
package modeler

import java.io.{ File, FileInputStream, FileOutputStream, BufferedInputStream }
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary.{ OWL, DC_11 => DC }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import util.{ Logging, Version, DateTime, URI }

import modeler.{ CimVocabulary => CIM }

/**
 * @author ShiZhan
 * translate archive file contents into semantic model
 * can be used with Directory modeler to reveal the detail of a file system
 */

object Archive extends Modeler with Logging {

  override val key = "arc"

  override val usage = "<archive> => [triples],\n\t\tcurrently support [ar, cpio, jar, tar, zip]."

  def run(options: Array[String]) = {
    val input = options(0)
    val f = new File(input)
    if (!f.exists)
      logger.error("input source does not exist")
    else if (!f.isFile)
      logger.error("input source is not file")
    else {
      logger.info("Model zipped file [{}]", f.getAbsolutePath)

      val base = URI.fromHost

      val m = ModelFactory.createOntologyModel

      m.setNsPrefix(key, base + "#")
      m.setNsPrefix(CimSchema.key, CIM.NS)
      m.createOntology(base)
        .addProperty(DC.date, DateTime.get, XSDdateTime)
        .addProperty(DC.description, "TriGraM Archive model", XSDstring)
        .addProperty(OWL.versionInfo, Version.get, XSDstring)
        .addProperty(OWL.imports, CIM.IMPORT("CIM_Directory"))
        .addProperty(OWL.imports, CIM.IMPORT("CIM_DataFile"))
        .addProperty(OWL.imports, CIM.IMPORT("CIM_DirectoryContainsFile"))

      val arcURI = URI.fromFile(f)
      val bFIS = new BufferedInputStream(new FileInputStream(f))
      val aSF = new ArchiveStreamFactory
      val aIS = aSF.createArchiveInputStream(bFIS)
      val iAIS = Iterator.continually { aIS.getNextEntry }.takeWhile(_ != null)

      val arcPath = f.getAbsolutePath
      val arcSize = f.length.toString
      val arcModi = DateTime.get(f.lastModified)
      val arcHash = f.hashCode.toHexString
      val arcFile = m.createIndividual(arcURI, CIM.CLASS("CIM_Directory"))
        .addProperty(CIM.PROP("Name"), arcPath, XSDnormalizedString)
        .addProperty(CIM.PROP("FileSize"), arcSize, XSDunsignedLong)
        .addProperty(CIM.PROP("LastModified"), arcModi, XSDdateTime)
        .addProperty(CIM.PROP("InstanceID"), arcHash, XSDnormalizedString)

      val containFile = m.createIndividual(arcURI + ".dcf",
        CIM.CLASS("CIM_DirectoryContainsFile"))
        .addProperty(CIM.PROP("GroupComponent"), arcFile)

      for (e <- iAIS) {
        val name = e.getName
        val uri = URI.fromString(arcPath + '/' + name)
        val size = e.getSize.toString
        val modi = DateTime.get(e.getLastModifiedDate)
        val hash = e.hashCode.toHexString
        val cimClass =
          if (e.isDirectory) CIM.CLASS("CIM_Directory") else CIM.CLASS("CIM_DataFile")
        val entry = m.createIndividual(uri, cimClass)
          .addProperty(CIM.PROP("Name"), name, XSDnormalizedString)
          .addProperty(CIM.PROP("FileSize"), size, XSDunsignedLong)
          .addProperty(CIM.PROP("LastModified"), modi, XSDdateTime)
          .addProperty(CIM.PROP("InstanceID"), hash, XSDnormalizedString)

        containFile.addProperty(CIM.PROP("PartComponent"), entry)
      }

      val output = input + "-model.owl"
      m.write(new FileOutputStream(output), "RDF/XML-ABBREV")

      logger.info("[{}] triples generated in [{}]", m.size, output)
    }
  }

}