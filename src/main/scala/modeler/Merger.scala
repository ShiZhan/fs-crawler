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
    val in = FileManager.get.open(modelFile)
    m.read(in, "")
    val importURIs = m.listObjectsOfProperty(OWL.imports).toList
    val importFiles =
      for (i <- importURIs)
        yield CIM.PATH_BASE + i.toString.substring(CIM.NS.size)
    importFiles foreach println
  }

}