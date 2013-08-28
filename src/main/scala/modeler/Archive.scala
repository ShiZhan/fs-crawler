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

/**
 * @author ShiZhan
 * translate archive file contents into semantic model
 * can be used with Directory modeler to reveal the detail of a file system
 */

object Archive extends Modeler with Logging {

  override val key = "arc"

  override val usage = "Translate archive file contents (ar|cpio|jar|tar|zip)"

  def run(input: String, output: String) = {
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

      m.setNsPrefix("prop", DIR.propNS)
      m.createResource(base, OWL.Ontology)
        .addProperty(DC.date, DateTime.get, XSDdateTime)
        .addProperty(DC.description, "TriGraM Archive model", XSDstring)
        .addProperty(OWL.versionInfo, Version.get, XSDstring)
        .addProperty(OWL.imports, DIR.IMPORT("CIM_Directory"))
        .addProperty(OWL.imports, DIR.IMPORT("CIM_DataFile"))
        .addProperty(OWL.imports, DIR.IMPORT("CIM_DirectoryContainsFile"))

      val bFIS = new BufferedInputStream(new FileInputStream(f))
      val aSF = new ArchiveStreamFactory
      val aIS = aSF.createArchiveInputStream(bFIS)
      val iAIS = Iterator.continually { aIS.getNextEntry }.takeWhile(_ != null)

      val archiveFile = m.createResource(ns + aIS, OWL2.NamedIndividual)
        .addProperty(RDF.`type`, DIR.CLASS("CIM_Directory"))
        .addProperty(DIR.PROP("Name"), f.getAbsolutePath, XSDnormalizedString)
        .addProperty(DIR.PROP("FileSize"), f.length.toString, XSDunsignedLong)
        .addProperty(DIR.PROP("LastModified"), DateTime.get(f.lastModified), XSDdateTime)

      val containFile = m.createResource(ns + aIS + "_dcf", OWL2.NamedIndividual)
        .addProperty(RDF.`type`, DIR.CLASS("CIM_DirectoryContainsFile"))
        .addProperty(DIR.PROP("GroupComponent"), archiveFile)

      for (e <- iAIS) {
        val name = e.getName
        val uri = ns + Hash.getMD5(name)
        val size = e.getSize.toString
        val lastM = DateTime.get(e.getLastModifiedDate)
        val cimClass =
          if (e.isDirectory) DIR.CLASS("CIM_Directory")
          else DIR.CLASS("CIM_DataFile")
        val entry = m.createResource(uri, OWL2.NamedIndividual)
          .addProperty(RDF.`type`, cimClass)
          .addProperty(DIR.PROP("Name"), name, XSDnormalizedString)
          .addProperty(DIR.PROP("FileSize"), size, XSDunsignedLong)
          .addProperty(DIR.PROP("LastModified"), lastM, XSDdateTime)

        containFile.addProperty(DIR.PROP("PartComponent"), entry)
      }

      m.write(new FileOutputStream(output), "RDF/XML-ABBREV")

      logger.info("[{}] triples generated", m.size)
    }
  }

}