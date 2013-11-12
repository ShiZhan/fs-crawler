/**
 * Model Merger
 */
package modeler

import scala.collection.JavaConversions._
import com.hp.hpl.jena.rdf.model.{ ModelFactory, Model }
import com.hp.hpl.jena.util.FileManager
import com.hp.hpl.jena.vocabulary.{ OWL, DC_11 => DC }

import modeler.{ CimVocabulary => CIM }
import util.Logging

/**
 * @author ShiZhan
 * CIM Model Merger
 * collect CimSchemaEx sub-models dependency from owl:import chain
 */
object Merger extends Logging {

  private def load(fileName: String) = {
    val m = ModelFactory.createDefaultModel
    val mFIS = FileManager.get.open(fileName)
    m.read(mFIS, "")
  }

  private def join(models: List[Model]) = {
    val baseModel = ModelFactory.createDefaultModel
    (baseModel /: models) { (r, m) => r union m }
  }

  private def readImports(modelFile: String): List[String] = {
    val m = load(modelFile)
    val importURIs = m.listObjectsOfProperty(OWL.imports).toList
    val importFiles =
      importURIs.map(CIM.PATH_BASE + _.toString.substring(CIM.NS.size)).toList
    m.close
    if (importFiles.isEmpty) List()
    else importFiles ::: importFiles.flatMap(readImports)
  }

  def gather(modelFile: String) = {
    val files = readImports(modelFile).distinct

    logger.info("[{}] CIM classes imported:", files.length)
    files foreach println

    val gatheredModel = join({ files :+ modelFile } map load)
    val stmtImport = gatheredModel.listStatements(null, OWL.imports, null)
    gatheredModel.remove(stmtImport)

    val gatheredFile = modelFile + "-gathered.owl"
    val mFOS = new java.io.FileOutputStream(gatheredFile)
    gatheredModel.write(mFOS, "RDF/XML-ABBREV")
    mFOS.close

    logger.info("wrote [{}] triples to [{}]", gatheredModel.size, gatheredFile)
  }

  def combine(modelFiles: List[String]) {
    val combinedModel = join(modelFiles map load)

    val combinedFile = modelFiles.head + "-combined.owl"
    val mFOS = new java.io.FileOutputStream(combinedFile)
    combinedModel.write(mFOS, "RDF/XML-ABBREV")
    mFOS.close

    logger.info("wrote [{}] triples to [{}]", combinedModel.size, combinedFile)
  }

}