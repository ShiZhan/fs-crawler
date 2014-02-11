/**
 * Modeler to translate directory
 */
package modeler

/**
 * @author ShiZhan
 * translate directory structure into N-Triples
 */
object DirectoryModels {
  import java.io.File
  import com.hp.hpl.jena.rdf.model.Model
  import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
  import common.URI
  import Vocabulary._
  import helper.DateTime

  implicit class FileModel(f: File) {
    val name = f.getAbsolutePath
    val size = f.length.toString
    val lastMod = DateTime.get(f.lastModified)
    val canRead = f.canRead.toString
    val canWrite = f.canWrite.toString
    val canExecute = f.canExecute.toString
    val isDirectory = f.isDirectory
    val uri = URI.fromFile(f)

    def -->(m: Model) = {
      val res = m.createResource(uri)
        .addProperty(PROP("name"), name, XSDnormalizedString)
        .addProperty(PROP("fileSize"), size, XSDunsignedLong)
        .addProperty(PROP("lastMod"), lastMod, XSDdateTime)
        .addProperty(PROP("canRead"), canRead, XSDboolean)
        .addProperty(PROP("canWrite"), canWrite, XSDboolean)
        .addProperty(PROP("canExecute"), canExecute, XSDboolean)
        .addProperty(PROP("isDirectory"), isDirectory.toString, XSDboolean)
      if (isDirectory) for (file <- f.listFiles)
        res.addProperty(PROP("contains"), m.getResource(URI.fromFile(file)))
    }
  }
}

object Directory extends Modeler with helper.Logging {
  import DirectoryModels._
  import common.FileEx._
  import common.ModelEx._
  import common.Gauge.ArrayOperations

  val key = "dir"

  val usage = "[input] [output.n3] => output.n3"

  def run(options: List[String]) =
    options match {
      case input :: output :: Nil => translate(input, output)
      case _ => logger.error("incorrect options: [{}]", options)
    }

  private def translate(input: String, output: String) = {
    logger.info("creating model ...")

    val m = createDefaultModel
    val root = input.toFile
    root --> m
    root.flatten.forAllDo(_ --> m)
    m.store(output.setExt("n3"), "N3")
  }
}