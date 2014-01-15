/**
 * Model Merger
 */
package modeler

/**
 * @author ShiZhan
 * CIM Model Merger
 * collect CimSchemaEx sub-models dependency from owl:import chain
 */
object Merger extends helper.Logging {
  import scala.collection.JavaConversions._
  import com.hp.hpl.jena.vocabulary.OWL
  import com.hp.hpl.jena.rdf.model.Model
  import cim.Vocabulary.{ isCimURI, PURL2FN }
  import common.ModelEx._

  private def readCimImports(modelFile: String): List[String] = {
    val m = load(modelFile)
    val importURIs = m.listObjectsOfProperty(OWL.imports).map(_.toString)
    val importFiles = importURIs.filter(isCimURI).map(PURL2FN).toList
    m.close
    if (importFiles.isEmpty) List()
    else importFiles ::: importFiles.flatMap(readCimImports)
  }

  private def clearCimImports(m: Model) = {
    val cimImports = m.listStatements(null, OWL.imports, null)
      .filter { i => isCimURI(i.getObject.toString) }.toList
    cimImports.foreach(m.remove)

    val cimOntologies = cimImports.map(_.getObject.asResource)
    cimOntologies.foreach { o => m.remove(m.listStatements(o, null, null)) }
  }

  def gather(modelFile: String) = {
    val files = readCimImports(modelFile).distinct

    logger.info("[{}] CIM classes imported:", files.length)

    files foreach println

    val baseModel = load(modelFile)
    val gatheredModel = (files load) join baseModel

    clearCimImports(gatheredModel)

    val gatheredFile = modelFile + "-gathered.owl"
    gatheredModel.store(gatheredFile)
  }

  def combine(modelFiles: List[String]) {
    val models = modelFiles load
    val combinedModel = models join
    val imports = models flatMap { _.listStatements(null, OWL.imports, null) }
    imports.foreach { combinedModel.add(_) }
    val combinedFile = modelFiles.head + "-combined.owl"
    combinedModel.store(combinedFile)
  }
}