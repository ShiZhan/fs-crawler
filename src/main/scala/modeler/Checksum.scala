/**
 * Modeler to translate directory content checksum into tree-structural model
 */
package modeler

/**
 * @author ShiZhan
 * Translate content characteristics of recognized data source into
 * tree-structural model [(checksum, id(path)), [(, ), ...]] for comparison
 */
object ChecksumModels {
  import java.io.File
  import com.hp.hpl.jena.ontology.OntModel
  import com.hp.hpl.jena.vocabulary.{ RDF, OWL, DC_11 => DC }
  import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
  import modeler.{ CimVocabulary => CIM }
  import helper.{ DateTime, URI, Version }
  import helper.FileEx.FileOps

  case class BlockModel(path: String, size: Long, md5sum: String) {
    def addTo(model: OntModel) = {
      model.createIndividual(URI.fromString(path), CIM.CLASS("CIM_DataFile"))
        .addProperty(CIM.PROP("Name"), path, XSDnormalizedString)
        .addProperty(CIM.PROP("FileSize"), size.toString, XSDunsignedLong)
        .addProperty(RDF.`type`, CIM.CLASS("CIM_FileSpecification"))
        .addProperty(CIM.PROP("MD5Checksum"), md5sum, XSDnormalizedString)
        .addProperty(CIM.PROP("FileName"), path, XSDnormalizedString)
    }
  }

  implicit class ChecksumModel(model: OntModel) {
    def set(base: String, nsPrefix: String) = {
      val ns = base + "CHK#"
      model.setNsPrefix(nsPrefix, ns)
      model.setNsPrefix(CimSchema.key, CIM.NS)
      model.createOntology(base)
        .addProperty(DC.date, DateTime.get, XSDdateTime)
        .addProperty(DC.description, "TriGraM checksum model", XSDstring)
        .addProperty(OWL.versionInfo, Version.get, XSDstring)
        .addProperty(OWL.imports, CIM.IMPORT("CIM_DataFile"))
        .addProperty(OWL.imports, CIM.IMPORT("CIM_OrderedComponent"))
        .addProperty(OWL.imports, CIM.IMPORT("CIM_FileSpecification"))
      model
    }
  }

  implicit class FileChecksumModel(file: File) {
    val md5sum = file.checksum
    val path = file.getAbsolutePath
    val size = file.length
    def addTo(model: OntModel) = BlockModel(path, size, md5sum) addTo model
  }

  case class ChunkChecksumModel(file: File, chunkSize: Long) {
    def addTo(model: OntModel) = {
      val chunked = file addTo model
      for ((i, s, m) <- file.checksum(chunkSize)) {
        val chunk = BlockModel(file.getAbsolutePath + "." + i, s, m) addTo model
        chunk.addProperty(RDF.`type`, CIM.CLASS("CIM_OrderedComponent"))
          .addProperty(CIM.PROP("GroupComponent"), chunked)
          .addProperty(CIM.PROP("PartComponent"), chunk)
          .addProperty(CIM.PROP("AssignedSequence"), i.toString, XSDunsignedInt)
      }
    }
  }
}

object Checksum extends Modeler with helper.Logging {
  import java.io.{ File, FileOutputStream }
  import com.hp.hpl.jena.rdf.model.ModelFactory
  import ChecksumModels._
  import helper.FileEx.FileOps
  import helper.URI

  override val key = "chk"
  override val usage = "<source> <output.owl> [<chunk size: Bytes>] => [output.owl]"
  def run(options: Array[String]) = {
    options.toList match {
      case fileName :: output :: Nil => {
        val source = new File(fileName).flatten
        translate(source, output)
      }
      case fileName :: output :: chunkSizeStr :: Nil => {
        val chunkSize = chunkSizeStr.toLong
        val source = new File(fileName).flatten
        translate(source, output, chunkSize)
      }
      case _ => logger.error("parameter error: [{}]", options)
    }
  }

  private def translate(files: Array[File], output: String) = {
    logger.info("Modeling")

    val m = ModelFactory.createOntologyModel.set(URI.fromHost, key)
    files.foreach { f => if (f.isFile) f addTo m }
    m.write(new FileOutputStream(output), "RDF/XML-ABBREV")

    logger.info("[{}] triples generated in [{}]", m.size, output)
  }

  private def translate(files: Array[File], output: String, chunkSize: Long) = {
    logger.info("Modeling")

    val m = ModelFactory.createOntologyModel.set(URI.fromHost, key)
    files.foreach { f => if (f.isFile) ChunkChecksumModel(f, chunkSize) addTo m }
    m.write(new FileOutputStream(output), "RDF/XML-ABBREV")

    logger.info("[{}] triples generated in [{}]", m.size, output)
  }
}