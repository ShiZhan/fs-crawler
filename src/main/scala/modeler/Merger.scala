/**
 * Model Merger
 */
package modeler

import scala.collection.JavaConversions._
import com.hp.hpl.jena.vocabulary.OWL
import CimVocabulary.{ isCimURI, PURL2FN }
import modeler.ModelManager._
import util.Logging

/**
 * @author ShiZhan
 * CIM Model Merger
 * collect CimSchemaEx sub-models dependency from owl:import chain
 */
object Merger extends Logging {

  private def readCimImports(modelFile: String): List[String] = {
    val m = load(modelFile)
    val importURIs = m.listObjectsOfProperty(OWL.imports).map(_.toString)
    val importFiles = importURIs.filter(isCimURI).map(PURL2FN).toList
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

    val importStatements = gatheredModel.listStatements(null, OWL.imports, null)
    val cimImports =
      importStatements.filter { i => isCimURI(i.getObject.toString) }.toList
    cimImports.foreach(gatheredModel.remove)

    val cimOntologies = cimImports.map(_.getObject.asResource)
    cimOntologies.foreach { o =>
      gatheredModel.remove(gatheredModel.listStatements(o, null, null))
    }

    val gatheredFile = modelFile + "-gathered.owl"
    gatheredModel.write(gatheredFile)

    logger.info("wrote [{}] triples to [{}]", gatheredModel.size, gatheredFile)
  }

  def combine(modelFiles: List[String]) {
    val models = modelFiles load
    val combinedModel = models join
    val imports = models flatMap { _.listStatements(null, OWL.imports, null) }
    imports.foreach { i => combinedModel.add(i) }
    val combinedFile = modelFiles.head + "-combined.owl"
    combinedModel.write(combinedFile)

    logger.info("wrote [{}] triples to [{}]", combinedModel.size, combinedFile)
  }

}