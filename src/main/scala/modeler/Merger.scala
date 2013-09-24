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

  private def gather(base: String): List[String] = {
    val m = ModelFactory.createDefaultModel
    val mFIS = FileManager.get.open(base)
    m.read(mFIS, "")
    val importURIs = m.listObjectsOfProperty(OWL.imports).toList
    val importFiles =
      importURIs.map(CIM.PATH_BASE + _.toString.substring(CIM.NS.size)).toList
    m.close
    if (importFiles.isEmpty) List()
    else importFiles ::: importFiles.flatMap(gather)
  }

  def run(baseModelFile: String) = {
    val files = gather(baseModelFile).distinct

    logger.info("loading imported models: [{}]", files mkString ", ")

    val models = files map {
      f =>
        val m = ModelFactory.createDefaultModel
        val mFIS = FileManager.get.open(f)
        m.read(mFIS, "")
    } toList

    val baseModel = ModelFactory.createDefaultModel
    val baseModelFIS = FileManager.get.open(baseModelFile)
    baseModel.read(baseModelFIS, "")
    val mergedModel = (baseModel /: models) { (r, m) => r union m }
    val importStmts = mergedModel.listStatements(null, OWL.imports, null)
    mergedModel.remove(importStmts)
    val mFOS = new java.io.FileOutputStream(baseModelFile + "-merged.owl")
    mergedModel.write(mFOS, "RDF/XML-ABBREV")

    logger.info("[{}] triples merged into [{}]",
      mergedModel.size, baseModelFile + "-merged.owl")
  }
}