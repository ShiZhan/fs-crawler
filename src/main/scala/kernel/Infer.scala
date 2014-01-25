/**
 * Apache Jena infer operations
 */
package kernel

/**
 * @author ShiZhan
 * Apache Jena infer operations
 */
object Infer extends helper.Logging {
  import com.hp.hpl.jena.rdf.model.{ Model, ModelFactory, InfModel }
  import com.hp.hpl.jena.ontology.{ OntModel, OntModelSpec }
  import com.hp.hpl.jena.reasoner.ReasonerRegistry
  import com.hp.hpl.jena.reasoner.rulesys.{ GenericRuleReasoner, Rule }
  import common.ModelEx._
  import helper.BuildIn

  val rEmpty = Rule.parseRules("")

  val rClassification =
    Rule.parseRules(BuildIn.getString("rules/classification.rule"))

  val defaultRules = rClassification

  def parseRules(ruleString: String) =
    try {
      Rule.parseRules(ruleString)
    } catch {
      case e: Exception => logger.error(e.toString); rEmpty
    }

  implicit class InferAsOntology(m: Model) {
    def infer(rules: java.util.List[Rule]) = {
      val reasoner = new GenericRuleReasoner(rules)
      val ontModelSpec = OntModelSpec.OWL_MEM
      ontModelSpec.setReasoner(reasoner)
      ModelFactory.createOntologyModel(ontModelSpec, m)
    }
  }

  implicit class InfModelValidate(infModel: InfModel) {
    def validateAndSave(output: String) = {
      val validity = infModel.validate
      if (validity.isValid)
        infModel.getDeductionsModel.store(output)
      else {
        val reports = Iterator.continually { validity.getReports }.takeWhile(_.hasNext)
        for (r <- reports) logger.info(r.toString)
      }
    }
  }
}