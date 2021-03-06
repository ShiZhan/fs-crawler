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

  implicit class ArchiveModel(m: Model) {
    def addArcFile(f: File) = {
      val path = f.getAbsolutePath
      val uri = URI.fromFile(f)
      val size = f.length.toString
      val modi = DateTime.get(f.lastModified)
      val arcFile = m.createResource(uri)
        .addProperty(PROP("name"), path, XSDnormalizedString)
        .addProperty(PROP("fileSize"), size, XSDunsignedLong)
        .addProperty(PROP("lastMod"), modi, XSDdateTime)
    }

    def addArcEntry(archiveEntryChecksum: ArcEntryChecksum) = {
      val ArcEntryChecksum(e, arcivePath, checksum) = archiveEntryChecksum
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
  import common.Gauge.ArrayOperations

  val key = "arc"

  val usage = "[input] [output.n3] => output.n3, support zip, gzip, bzip & 7z."

  def run(options: List[String]) = options match {
    case input :: output :: Nil => translate(input, output)
    case _ => logger.error("parameter error: [{}]", options)
  }

  private def translate(input: String, output: String) = {
    val m = createDefaultModel
    input.toFile.flatten.foreachDo { f =>
      if (f.isFile)
        getChecker(f) match {
          case checker: arcChecker => {
            m.addArcFile(f)
            checker(f).foreach(m.addArcEntry)
          }
          case _ =>
        }
    }
    m.store(output.setExt("n3"), "N3")
  }
}