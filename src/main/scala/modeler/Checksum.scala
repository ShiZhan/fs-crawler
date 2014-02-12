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

  implicit class ChecksumModel(m: Model) {
    private def addBlock(path: String, size: Long, md5sum: String) = {
      m.createResource(URI.fromString(path))
        .addProperty(PROP("name"), path, XSDnormalizedString)
        .addProperty(PROP("fileSize"), size.toString, XSDunsignedLong)
        .addProperty(PROP("md5"), md5sum, XSDnormalizedString)
    }

    def addFile(file: File) =
      addBlock(file.getAbsolutePath, file.length, file.checksum)

    def addFileChunks(file: File, chunkSize: Long) = {
      val chunked = addFile(file)
      for ((i, s, c) <- file.checksum(chunkSize)) {
        val chunk = addBlock(file.getAbsolutePath + "." + i, s, c)
        chunked.addProperty(PROP("contains"), chunk)
      }
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
    input.toFile.flatten.foreachDo(f => if (f.isFile) m.addFile(f))
    m.store(output.setExt("n3"), "N3")
  }

  private def translate(input: String, output: String, chunkSize: Long) = {
    val m = createDefaultModel
    input.toFile.flatten.foreachDo(f => if (f.isFile) m.addFileChunks(f, chunkSize))
    m.store(output.setExt("n3"), "N3")
  }
}