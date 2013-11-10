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

  private def readImports(baseModelFile: String): List[String] = {
    val m = loadModel(baseModelFile)
    val importURIs = m.listObjectsOfProperty(OWL.imports).toList
    val importFiles =
      importURIs.map(CIM.PATH_BASE + _.toString.substring(CIM.NS.size)).toList
    m.close
    if (importFiles.isEmpty) List()
    else importFiles ::: importFiles.flatMap(readImports)
  }

  def gather(baseModelFile: String) = {
    val files = readImports(baseModelFile).distinct

    logger.info("[{}] CIM classes imported:", files.length)
    files foreach println

    val models = files map { loadModel(_) } toList
    val baseModel = loadModel(baseModelFile)
    val gatheredModel = (baseModel /: models) { (r, m) => r union m }
    val importStmts = gatheredModel.listStatements(null, OWL.imports, null)
    gatheredModel.remove(importStmts)
    val gatheredFile = baseModelFile + "-gathered.owl"
    val mFOS = new java.io.FileOutputStream(gatheredFile)
    gatheredModel.write(mFOS, "RDF/XML-ABBREV")

    logger.info("wrote [{}] triples to [{}]", gatheredModel.size, gatheredFile)
  }

  def combine(modelFiles: List[String]) {
    val models = modelFiles map { loadModel(_) } toList

    val combinedFile = "" + "combined.owl"
    logger.info("wrote [{}] triples to [{}]", 0, combinedFile)
  }

}