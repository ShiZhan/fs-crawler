/**
 * Thinker module
 */
package core

import java.io.FileOutputStream
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.util.FileManager
import util.Logging

/**
 * @author ShiZhan
 * Wrapper for Jena RDFS reasoner
 */
object Thinker extends Logging {

  def infer(schema: String, data: String) = {
    val s = FileManager.get.loadModel("file:" + schema)
    val d = FileManager.get.loadModel("file:" + data)
    ModelFactory.createRDFSModel(s, d)
  }

  def inferAndSave(schema: String, data: String, output: String) = {
    val infModel = infer(schema, data)
    val validity = infModel.validate
    if (validity.isValid) {
      val m = infModel.getRawModel
      m.write(new FileOutputStream(output), "RDF/XML-ABBREV")
      logger.info("[{}] triples written to [{}]", m.size, output)
    } else
      logger.info("inferred mode invalid")
  }

}