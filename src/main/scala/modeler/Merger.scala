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

  private def gather(base: String): List[String] = {
    val m = ModelFactory.createDefaultModel
    val mFIS = FileManager.get.open(base)
    m.read(mFIS, "")
    val importURIs = m.listObjectsOfProperty(OWL.imports).toList
    val importFiles =
      {for (i <- importURIs)
        yield CIM.PATH_BASE + i.toString.substring(CIM.NS.size)}.toList
    m.close
    importFiles ++ importFiles.flatMap(gather)
  }

  def run(modelFile: String) = {
    val modelList = gather(modelFile).distinct
    modelList map {
      f =>
        val im = ModelFactory.createDefaultModel
        val imFIS = FileManager.get.open(f)
        im.read(imFIS, "")
    } toList

    println("")

//    m.remove(m.listStatements(null, OWL.imports, null))
  }
}