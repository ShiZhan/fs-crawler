/**
 * Model operations DSL
 */
package modeler

import com.hp.hpl.jena.rdf.model.{ ModelFactory, Model }
import com.hp.hpl.jena.util.FileManager

/**
 * @author ShiZhan
 * Model Seq operation
 * 1. join:  combine all models in Seq
 *           combine all models in Seq to base model
 * 2. load:  load model from given file
 *           load model from given file with absolute base URI
 *    NOTE:  use file input stream API for better compatibility
 * 3. write: encapsulate file output stream API for model writing
 *           in given format
 */
class Models(models: Seq[Model]) {
  def join = {
    val baseModel = ModelFactory.createDefaultModel
    (baseModel /: models) { (r, m) => r union m }
  }

  def join(baseModel: Model) = {
    (baseModel /: models) { (r, m) => r union m }
  }
}

class ModelFiles(fileNames: Seq[String]) {
  def load = fileNames map ModelManager.load
  def load(base: String) = fileNames map { ModelManager.load(_, base) }
}

class ModelWrapper(m: Model) {
  def write(fileName: String) = {
    val fos = new java.io.FileOutputStream(fileName)
    m.write(fos, "RDF/XML-ABBREV")
    fos.close
  }

  def write(fileName: String, format: String) = {
    val fos = new java.io.FileOutputStream(fileName)
    m.write(fos, format)
    fos.close
  }
}

object ModelManager {
  implicit def modelSeq2models(models: Seq[Model]) = new Models(models)
  implicit def fileNames2models(fileNames: Seq[String]) = new ModelFiles(fileNames)
  implicit def model2wrapper(m: Model) = new ModelWrapper(m)

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
}