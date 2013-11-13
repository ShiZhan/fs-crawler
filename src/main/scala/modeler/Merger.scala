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
 */
object Merger extends Logging {

  private def isCimModelURI = (u: String) => u.startsWith(CIM.NS)

  private def cimModelURI2Local =
    (u: String) => CIM.PATH_BASE + u.substring(CIM.NS.size)

  private def readImports(modelFile: String): List[String] = {
    val m = load(modelFile)
    val importURIs = m.listObjectsOfProperty(OWL.imports).map(_.toString)
    val importFiles = importURIs.filter(isCimModelURI).map(cimModelURI2Local).toList
    m.close
    if (importFiles.isEmpty) List()
    else importFiles ::: importFiles.flatMap(readImports)
  }

  def gather(modelFile: String) = {
    val files = readImports(modelFile).distinct

    logger.info("[{}] CIM classes imported:", files.length)
    files foreach println

    val baseModel = load(modelFile)
    val gatheredModel = (files load) join baseModel
    val stmtImport = gatheredModel.listStatements(null, OWL.imports, null)
    gatheredModel.remove(stmtImport)
    val gatheredFile = modelFile + "-gathered.owl"
    gatheredModel.write(gatheredFile)

    logger.info("wrote [{}] triples to [{}]", gatheredModel.size, gatheredFile)
  }

  def combine(modelFiles: List[String]) {
    val combinedModel = (modelFiles load) join
    val combinedFile = modelFiles.head + "-combined.owl"
    combinedModel.write(combinedFile)

    logger.info("wrote [{}] triples to [{}]", combinedModel.size, combinedFile)
  }

}