/**
 * Model Merger
 */
package modeler

import scala.collection.JavaConversions._
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.util.FileManager
import com.hp.hpl.jena.vocabulary.OWL

import modeler.{ CimVocabulary => CIM }
import util.Logging

/**
 * @author ShiZhan
 * CIM Model Merger
 * collect CimSchemaEx sub-models dependency from owl:import chain
 */
object Merger extends Logging {

  private def loadModel(fileName: String) = {
    val m = ModelFactory.createDefaultModel
    val mFIS = FileManager.get.open(fileName)
    m.read(mFIS, "")
  }

  private def gather(base: String): List[String] = {
    val m = loadModel(base)
    val importURIs = m.listObjectsOfProperty(OWL.imports).toList
    val importFiles =
      importURIs.map(CIM.PATH_BASE + _.toString.substring(CIM.NS.size)).toList
    m.close
    if (importFiles.isEmpty) List()
    else importFiles ::: importFiles.flatMap(gather)
  }

  def merge(modelFile: String) = {
    val files = gather(modelFile).distinct

    logger.info("[{}] CIM classes imported:", files.length)
    files foreach println

    val models = files map { loadModel(_) } toList
    val baseModel = loadModel(modelFile)
    val mergedModel = (baseModel /: models) { (r, m) => r union m }
    val importStmts = mergedModel.listStatements(null, OWL.imports, null)
    mergedModel.remove(importStmts)
    val mFOS = new java.io.FileOutputStream(modelFile + "-merged.owl")
    mergedModel.write(mFOS, "RDF/XML-ABBREV")

    logger.info("[{}] triples merged into [{}]",
      mergedModel.size, modelFile + "-merged.owl")
  }
}