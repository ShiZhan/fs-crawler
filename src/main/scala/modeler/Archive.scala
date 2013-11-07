/**
 * Modeler for compressed resources
 */
package modeler

import java.io.{
  File,
  FileInputStream,
  FileOutputStream,
  InputStreamReader,
  BufferedInputStream
}
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary.{
  RDF,
  RDFS,
  OWL,
  OWL2,
  DC_11 => DC,
  DCTerms => DT
}
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import util.{ Logging, Version, DateTime, Hash }

import modeler.{ CimVocabulary => CIM }

/**
 * @author ShiZhan
 * translate archive file contents into semantic model
 * can be used with Directory modeler to reveal the detail of a file system
 */

object Archive extends Modeler with Logging {

  override val key = "arc"

  override val usage = "<archive file> => [triples], currently support [ar, cpio, jar, tar, zip]."

  def run(options: Array[String]) = {
    val input = options(0)
    val f = new File(input)
    if (!f.exists)
      logger.error("input source does not exist")
    else if (!f.isFile)
      logger.error("input source is not file")
    else {
      logger.info("Model zipped file [{}]", f.getAbsolutePath)

      val base = f.toURI.toString
      val ns = base + "#"

      val m = ModelFactory.createDefaultModel

      m.setNsPrefix(key, ns)
      m.setNsPrefix(CimSchema.key, CIM.NS)
      m.createResource(base, OWL.Ontology)
        .addProperty(DC.date, DateTime.get, XSDdateTime)
        .addProperty(DC.description, "TriGraM Archive model", XSDstring)
        .addProperty(OWL.versionInfo, Version.get, XSDstring)
        .addProperty(OWL.imports, CIM.IMPORT("CIM_Directory"))
        .addProperty(OWL.imports, CIM.IMPORT("CIM_DataFile"))
        .addProperty(OWL.imports, CIM.IMPORT("CIM_DirectoryContainsFile"))

      val bFIS = new BufferedInputStream(new FileInputStream(f))
      val aSF = new ArchiveStreamFactory
      val aIS = aSF.createArchiveInputStream(bFIS)
      val iAIS = Iterator.continually { aIS.getNextEntry }.takeWhile(_ != null)

      val archiveFile = m.createResource(ns + aIS, OWL2.NamedIndividual)
        .addProperty(RDF.`type`, CIM.CLASS("CIM_Directory"))
        .addProperty(CIM.PROP("Name"), f.getAbsolutePath, XSDnormalizedString)
        .addProperty(CIM.PROP("FileSize"), f.length.toString, XSDunsignedLong)
        .addProperty(CIM.PROP("LastModified"), DateTime.get(f.lastModified), XSDdateTime)
        .addProperty(CIM.PROP("InstanceID"), f.hashCode.toHexString, XSDnormalizedString)

      val containFile = m.createResource(ns + aIS + "_dcf", OWL2.NamedIndividual)
        .addProperty(RDF.`type`, CIM.CLASS("CIM_DirectoryContainsFile"))
        .addProperty(CIM.PROP("GroupComponent"), archiveFile)

      for (e <- iAIS) {
        val name = e.getName
        val uri = ns + Hash.getMD5(name)
        val size = e.getSize.toString
        val lastM = DateTime.get(e.getLastModifiedDate)
        val hash = e.hashCode.toHexString
        val cimClass =
          if (e.isDirectory) CIM.CLASS("CIM_Directory")
          else CIM.CLASS("CIM_DataFile")
        val entry = m.createResource(uri, OWL2.NamedIndividual)
          .addProperty(RDF.`type`, cimClass)
          .addProperty(CIM.PROP("Name"), name, XSDnormalizedString)
          .addProperty(CIM.PROP("FileSize"), size, XSDunsignedLong)
          .addProperty(CIM.PROP("LastModified"), lastM, XSDdateTime)
          .addProperty(CIM.PROP("InstanceID"), hash, XSDnormalizedString)

        containFile.addProperty(CIM.PROP("PartComponent"), entry)
      }

      val output = input + "-model.owl"
      m.write(new FileOutputStream(output), "RDF/XML-ABBREV")

      logger.info("[{}] triples generated in [{}]", m.size, output)
    }
  }

}