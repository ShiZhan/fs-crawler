/**
 * Modeler for compressed resources
 */
package modeler

import java.io.{ File, FileInputStream, FileOutputStream, BufferedInputStream }
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary.{ OWL, OWL2, DC_11 => DC, RDF }
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

  override val usage = "<archive> => [triples],\n\t\t" +
    "currently support [ar, cpio, jar, tar, zip]."

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

  private def translate(f: File) = {
    logger.info("Model zipped file [{}]", f.getAbsolutePath)

    val base = URI.fromHost
    val m = ModelFactory.createDefaultModel
    m.setNsPrefix(key, base + "#")
    m.setNsPrefix(CimSchema.key, CIM.NS)
    m.createResource(base, OWL.Ontology)
      .addProperty(DC.date, DateTime.get, XSDdateTime)
      .addProperty(DC.description, "TriGraM Archive model", XSDstring)
      .addProperty(OWL.versionInfo, Version.get, XSDstring)
      .addProperty(OWL.imports, CIM.IMPORT("CIM_Directory"))
      .addProperty(OWL.imports, CIM.IMPORT("CIM_DataFile"))
      .addProperty(OWL.imports, CIM.IMPORT("CIM_Component"))

    val arcURI = URI.fromFile(f)
    val bFIS = new BufferedInputStream(new FileInputStream(f))
    val aSF = new ArchiveStreamFactory
    val aIS = aSF.createArchiveInputStream(bFIS)
    val iAIS = Iterator.continually { aIS.getNextEntry }.takeWhile(_ != null)

    val arcPath = f.getAbsolutePath
    val arcSize = f.length.toString
    val arcModi = DateTime.get(f.lastModified)
    val arcFile = m.createResource(arcURI, OWL2.NamedIndividual)
      .addProperty(RDF.`type`, CIM.CLASS("CIM_DataFile"))
      .addProperty(CIM.PROP("Name"), arcPath, XSDnormalizedString)
      .addProperty(CIM.PROP("FileSize"), arcSize, XSDunsignedLong)
      .addProperty(CIM.PROP("LastModified"), arcModi, XSDdateTime)
    arcFile.addProperty(RDF.`type`, CIM.CLASS("CIM_Component"))
      .addProperty(CIM.PROP("GroupComponent"), arcFile)

    for (e <- iAIS) {
      val name = e.getName
      val uri = URI.fromString(arcPath + '/' + name)
      val size = e.getSize.toString
      val modi = DateTime.get(e.getLastModifiedDate)
      val cimClass = CIM.CLASS(if (e.isDirectory) "CIM_Directory" else "CIM_DataFile")
      val entry = m.createResource(uri, OWL2.NamedIndividual)
        .addProperty(RDF.`type`, cimClass)
        .addProperty(CIM.PROP("Name"), name, XSDnormalizedString)
        .addProperty(CIM.PROP("FileSize"), size, XSDunsignedLong)
        .addProperty(CIM.PROP("LastModified"), modi, XSDdateTime)

      arcFile.addProperty(CIM.PROP("PartComponent"), entry)
    }

    val output = f.getAbsolutePath + "-model.owl"
    m.write(new FileOutputStream(output), "RDF/XML-ABBREV")

    logger.info("[{}] triples generated in [{}]", m.size, output)
  }

}