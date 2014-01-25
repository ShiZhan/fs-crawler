/**
 * Modeler for compressed files
 */
package modeler

/**
 * @author ShiZhan
 * translate archive file contents into semantic model
 */
object ArchiveModels {
  import java.io.File
  import com.hp.hpl.jena.rdf.model.Model
  import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
  import common.ArchiveEx.ArcEntryChecksum
  import common.URI
  import Vocabulary._
  import helper.DateTime

  implicit class ArcFileModel(f: File) {
    def -->(m: Model) = {
      val path = f.getAbsolutePath
      val uri = URI.fromFile(f)
      val size = f.length.toString
      val modi = DateTime.get(f.lastModified)
      val arcFile = m.createResource(uri)
        .addProperty(PROP("name"), path, XSDnormalizedString)
        .addProperty(PROP("fileSize"), size, XSDunsignedLong)
        .addProperty(PROP("lastMod"), modi, XSDdateTime)
    }
  }

  implicit class ArcEntryModel(archiveEntryChecksum: ArcEntryChecksum) {
    val ArcEntryChecksum(e, arcivePath, checksum) = archiveEntryChecksum
    def -->(m: Model) = {
      val path = e.getName
      val uri = URI.fromString(arcivePath + '/' + path)
      val fileSize = e.getSize.toString
      val lastMod = DateTime.get(e.getLastModifiedDate)
      val isDirectory = e.isDirectory
      val archive = m.getResource(URI.fromString(arcivePath))
      val entry = m.createResource(uri)
        .addProperty(PROP("name"), path, XSDnormalizedString)
        .addProperty(PROP("fileSize"), fileSize, XSDunsignedLong)
        .addProperty(PROP("lastMod"), lastMod, XSDdateTime)
        .addProperty(PROP("isDirectory"), isDirectory.toString, XSDboolean)
      if (!isDirectory)
        entry.addProperty(PROP("md5"), checksum, XSDnormalizedString)
      archive.addProperty(PROP("contains"), entry)
    }
  }
}

object Archive extends Modeler with helper.Logging {
  import ArchiveModels._
  import common.ArchiveEx._
  import common.FileEx._
  import common.ModelEx._

  val key = "arc"

  val usage = "[input] [output.n3] => output.n3, support zip, gzip, bzip & 7z."

  def run(options: List[String]) =
    options match {
      case input :: output :: Nil => translate(input, output)
      case _ => logger.error("parameter error: [{}]", options)
    }

  private def translate(input: String, output: String) = {
    logger.info("Model all supported archive file in [{}]", input)
    val m = createDefaultModel
    for (f <- input.toFile.flatten) {
      if (f.isFile) {
        getChecker(f) match {
          case checker: arcChecker => {
            f --> m
            for (e <- checker(f)) e --> m
          }
          case _ =>
        }
      }
    }
    m.store(output.setExt("n3"), "N3")
  }
}