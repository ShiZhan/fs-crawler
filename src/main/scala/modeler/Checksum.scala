/**
 * Modeler to translate directory content checksum into N-Triples
 */
package modeler

/**
 * @author ShiZhan
 * Translate content characteristics of data source into tree-structural model
 */
object ChecksumModels {
  import java.io.File
  import com.hp.hpl.jena.rdf.model.Model
  import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
  import Vocabulary._
  import common.URI
  import common.FileEx.FileOps

  case class BlockModel(path: String, size: Long, md5sum: String) {
    def -->(m: Model) = {
      m.createResource(URI.fromString(path))
        .addProperty(PROP("name"), path, XSDnormalizedString)
        .addProperty(PROP("fileSize"), size.toString, XSDunsignedLong)
        .addProperty(PROP("md5"), md5sum, XSDnormalizedString)
    }
  }

  implicit class FileChecksumModel(file: File) {
    val md5sum = file.checksum
    val path = file.getAbsolutePath
    val size = file.length
    def -->(model: Model) = BlockModel(path, size, md5sum) --> model
  }

  case class ChunkChecksumModel(file: File, chunkSize: Long) {
    def -->(m: Model) = {
      val chunked = file --> m
      for ((i, s, c) <- file.checksum(chunkSize))
        chunked.addProperty(PROP("contains"),
          BlockModel(file.getAbsolutePath + "." + i, s, c) --> m)
    }
  }
}

object Checksum extends Modeler with helper.Logging {
  import ChecksumModels._
  import common.FileEx._
  import common.ModelEx._
  import common.Gauge.ArrayOperations

  val key = "chk"

  val usage = "[input] [output.n3] <chunk size: Bytes> => output.n3"

  def run(options: List[String]) = {
    logger.info("Modeling")
    options match {
      case input :: output :: Nil =>
        translate(input, output)
      case input :: output :: chunkSizeStr :: Nil =>
        translate(input, output, chunkSizeStr.toLong)
      case _ => logger.error("parameter error: [{}]", options)
    }
  }

  private def translate(input: String, output: String) = {
    val m = createDefaultModel
    input.toFile.flatten.foreachDo(f => if (f.isFile) f --> m)
    m.store(output.setExt("n3"), "N3")
  }

  private def translate(input: String, output: String, chunkSize: Long) = {
    val m = createDefaultModel
    input.toFile.flatten.foreachDo(f => if (f.isFile) ChunkChecksumModel(f, chunkSize) --> m)
    m.store(output.setExt("n3"), "N3")
  }
}