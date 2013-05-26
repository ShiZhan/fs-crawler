/**
 * Thinker module
 */
package core

import java.io.FileOutputStream
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.reasoner.ReasonerRegistry
import com.hp.hpl.jena.util.FileManager
import util.Logging

/**
 * @author ShiZhan
 * Wrapper for Jena RDFS reasoner
 */
object Thinker extends Logging {

  def infer(schema: String, data: String) = {
    val fm = FileManager.get
    val s = fm.loadModel("file:" + schema)
    val d = fm.loadModel("file:" + data)
    ModelFactory.createRDFSModel(s, d)
  }

  def inferOWL(schema: String, data: String) = {
    val fm = FileManager.get
    val s = fm.loadModel("file:" + schema)
    val d = fm.loadModel("file:" + data)
    val baseReasoner = ReasonerRegistry.getOWLReasoner
    val reasoner = baseReasoner.bindSchema(s)
    ModelFactory.createInfModel(reasoner, d)
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