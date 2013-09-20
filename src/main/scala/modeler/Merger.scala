/**
 * Model Merger
 */
package modeler

import scala.collection.JavaConversions._
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.util.FileManager
import com.hp.hpl.jena.vocabulary.OWL

import modeler.{ CimVocabulary => CIM }

/**
 * @author ShiZhan
 * CIM Model Merger
 * based on the CimSchemaEx sub-models dependency
 * owl:import chain
 */
object Merger {

  def run(modelFile: String) = {
    val m = ModelFactory.createDefaultModel
    val mFIS = FileManager.get.open(modelFile)
    m.read(mFIS, "")
    val importURIs = m.listObjectsOfProperty(OWL.imports).toList
    val importFiles =
      for (i <- importURIs)
        yield CIM.PATH_BASE + i.toString.substring(CIM.NS.size)
    importFiles foreach println
    val importModels = importFiles map {
      f =>
        val im = ModelFactory.createDefaultModel
        val imFIS = FileManager.get.open(f)
        im.read(imFIS, "")
    }
    m.remove(m.listStatements(null, OWL.imports, null))
  }

}