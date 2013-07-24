/**
 * Thinker module
 */
package core

import java.io.FileOutputStream
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.reasoner.ReasonerRegistry
import com.hp.hpl.jena.util.FileManager
import com.hp.hpl.jena.ontology.OntModelSpec
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
    val baseReasoner = ReasonerRegistry.getRDFSReasoner
    val reasoner = baseReasoner.bindSchema(s)
    ModelFactory.createInfModel(reasoner, d)
  }

  def inferOWL(schema: String, data: String) = {
    val fm = FileManager.get
    val s = fm.loadModel("file:" + schema)
    val d = fm.loadModel("file:" + data)
    val baseReasoner = ReasonerRegistry.getOWLReasoner
    val reasoner = baseReasoner.bindSchema(s)
    ModelFactory.createInfModel(reasoner, d)
  }

  def inferRDFS(schema: String, data: String) = {
    val fm = FileManager.get
    val s = fm.loadModel("file:" + schema)
    val d = fm.loadModel("file:" + data)
    ModelFactory.createRDFSModel(s, d)
  }

  def inferOnt(input: String) = {
    val fm = FileManager.get
    val m = fm.loadModel("file:" + input)
    val reasoner = ReasonerRegistry.getOWLReasoner
    val ontModelSpec = OntModelSpec.OWL_DL_MEM
    ontModelSpec.setReasoner(reasoner)
    ModelFactory.createOntologyModel(ontModelSpec, m)
  }

  def inferAndSave(schema: String, data: String) = {
    logger.info("infer input [{}]", schema + '+' + data)
    val infModel = inferRDFS(schema, data)
    val validity = infModel.validate
    if (validity.isValid) {
      val m = infModel.getDeductionsModel
      val output = data + "-deduction.owl"
      m.write(new FileOutputStream(output), "RDF/XML-ABBREV")
      logger.info("[{}] triples written to [{}]", m.size, output)
    } else {
      logger.info("conflict:")
      val reports = Iterator.continually { validity.getReports }.takeWhile(_.hasNext)
      val detail = reports.map(_.next).mkString("\n")
      logger.info(detail)
    }
  }

}