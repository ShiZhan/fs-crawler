/**
 * Model Merger
 */
package modeler

/**
 * @author ShiZhan
 * CIM Model Merger
 * collect CimSchemaEx sub-models dependency from OWL:Imports chain
 */
object Merger {
  import scala.collection.JavaConversions._
  import com.hp.hpl.jena.rdf.model.Model
  import cim.Vocabulary.{ isCimURI, PURL2FN }
  import common.ModelEx._

  private def readCimImports(modelFile: String): List[String] = {
    val m = load(modelFile)
    val importFiles =
      m.getOWLImports.map { _.getObject.toString }.filter(isCimURI).map(PURL2FN).toList
    m.close
    if (importFiles.isEmpty) List()
    else importFiles ::: importFiles.flatMap(readCimImports)
  }

  implicit class ModelWithCimImports(m: Model) {
    def clearCimImports = {
      val cimImports = m.getOWLImports
        .filter { i => isCimURI(i.getObject.toString) }.toArray
      m.remove(cimImports)

      for (o <- cimImports.map(_.getObject.asResource))
        m.remove(m.listStatements(o, null, null))
      m
    }
  }

  def gather(modelFile: String) = {
    val files = readCimImports(modelFile).distinct
    println("[%d] CIM classes imported:".format(files.length))
    files foreach println

    { (files asModels) join load(modelFile) }.clearCimImports
      .store(modelFile + "-gathered.owl")
  }

  def combine(modelFiles: List[String]) {
    val models = modelFiles asModels
    val imports = models.flatMap(_.getOWLImports).toArray
    (models join).add(imports).store(modelFiles.head + "-combined.owl")
  }
}