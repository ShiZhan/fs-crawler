/**
 * Model Merger
 */
package modeler

import scala.collection.JavaConversions._
import com.hp.hpl.jena.vocabulary.OWL
import modeler.{ CimVocabulary => CIM }
import modeler.ModelManager._
import util.Logging

/**
 * @author ShiZhan
 * CIM Model Merger
 * collect CimSchemaEx sub-models dependency from owl:import chain
 * TODO: "gather" will remove all OWL.imports including those not from CIM models
 */
object Merger extends Logging {

  private def isCimModelURI = (u: String) => u.startsWith(CIM.NS)

  private def cimModelURI2Local =
    (u: String) => CIM.PATH_BASE + u.substring(CIM.NS.size)

  private def readCimImports(modelFile: String): List[String] = {
    val m = load(modelFile)
    val importURIs = m.listObjectsOfProperty(OWL.imports).map(_.toString)
    val importFiles = importURIs.filter(isCimModelURI).map(cimModelURI2Local).toList
    m.close
    if (importFiles.isEmpty) List()
    else importFiles ::: importFiles.flatMap(readCimImports)
  }

  def gather(modelFile: String) = {
    val files = readCimImports(modelFile).distinct

    logger.info("[{}] CIM classes imported:", files.length)
    files foreach println

    val baseModel = load(modelFile)
    val gatheredModel = (files load) join baseModel
    val imports = gatheredModel.listStatements(null, OWL.imports, null)
    gatheredModel.remove(imports)
    val gatheredFile = modelFile + "-gathered.owl"
    gatheredModel.write(gatheredFile)

    logger.info("wrote [{}] triples to [{}]", gatheredModel.size, gatheredFile)
  }

  def combine(modelFiles: List[String]) {
    val models = modelFiles load
    val combinedModel = models join
    val imports = models flatMap { _.listStatements(null, OWL.imports, null) }
    imports.foreach{ i => combinedModel.add(i) }
    val combinedFile = modelFiles.head + "-combined.owl"
    combinedModel.write(combinedFile)

    logger.info("wrote [{}] triples to [{}]", combinedModel.size, combinedFile)
  }

}