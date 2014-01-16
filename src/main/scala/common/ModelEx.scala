/**
 * Model operations DSL
 */
package common

/**
 * @author ShiZhan
 * Model extended operation
 * 1. load:  use file input stream API for better compatibility
 *           load with base
 * 2. join:  combine all models in Seq
 *           combine all models in Seq to base model
 * 3. load:  load model from given file name list
 *           load model from given file name list with absolute base URI
 * 4. write: encapsulate API for model writing in given format
 *           with given file name or instance
 *           with encoding
 */
object ModelEx {
  import java.io.{ File, FileOutputStream }
  import com.hp.hpl.jena.rdf.model.{ ModelFactory, Model }
  import com.hp.hpl.jena.util.FileManager
  import com.hp.hpl.jena.vocabulary.OWL
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

    def join(baseModel: Model) = (baseModel /: models) { (r, m) => r union m }
  }

  implicit class ModelFileOps(fileNames: Seq[String]) {
    def asModels = fileNames map load
    def asModels(base: String) = fileNames map { load(_, base) }
  }

  implicit class ModelOps(m: Model) extends helper.Logging {
    def getOWLImports = m.listStatements(null, OWL.imports, null)

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