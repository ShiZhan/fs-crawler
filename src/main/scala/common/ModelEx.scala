/**
 * Model operations DSL
 */
package common

/**
 * @author ShiZhan
 * Model Seq operation
 * 1. join:  combine all models in Seq
 *           combine all models in Seq to base model
 * 2. load:  load model from given file
 *           load model from given file with absolute base URI
 *    NOTE:  use file input stream API for better compatibility
 * 3. write: encapsulate API for model writing in given format
 */
object ModelEx {
  import java.io.{ File, FileOutputStream }
  import com.hp.hpl.jena.rdf.model.{ ModelFactory, Model }
  import com.hp.hpl.jena.util.FileManager
  import FileEx.FileOps

  def load(fileName: String) = {
    val m = ModelFactory.createDefaultModel
    val mFIS = FileManager.get.open(fileName)
    m.read(mFIS, "")
  }

  def load(fileName: String, base: String) = {
    val m = ModelFactory.createDefaultModel
    val mFIS = FileManager.get.open(fileName)
    m.read(mFIS, base)
  }

  implicit class ModelSeqOps(models: Seq[Model]) {
    def join = {
      val baseModel = ModelFactory.createDefaultModel
      (baseModel /: models) { (r, m) => r union m }
    }

    def join(baseModel: Model) = {
      (baseModel /: models) { (r, m) => r union m }
    }
  }

  implicit class ModelFileOps(fileNames: Seq[String]) {
    def load = fileNames map ModelEx.load
    def load(base: String) = fileNames map { ModelEx.load(_, base) }
  }

  implicit class ModelOps(m: Model) extends helper.Logging {
    def store(fileName: String) = {
      val fos = new File(fileName).getWriter("UTF-8")
      m.write(fos, "RDF/XML-ABBREV")
      fos.close
      logger.info("[{}] triples written to [{}]", m.size, fileName)
    }

    def store(file: File) = {
      val fos = file.getWriter("UTF-8")
      m.write(fos, "RDF/XML-ABBREV")
      fos.close
      logger.info("[{}] triples written to [{}]", m.size, file.getAbsolutePath)
    }

    def store(fileName: String, format: String) = {
      val fos = new File(fileName).getWriter("UTF-8")
      m.write(fos, format)
      fos.close
      logger.info("[{}] triples written to [{}]", m.size, fileName)
    }

    def store(file: File, format: String) = {
      val fos = file.getWriter("UTF-8")
      m.write(fos, format)
      fos.close
      logger.info("[{}] triples written to [{}]", m.size, file.getAbsolutePath)
    }
  }
}