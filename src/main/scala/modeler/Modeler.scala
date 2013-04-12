/**
 * Adapter Interface
 */
package modeler

import com.hp.hpl.jena.ontology.OntModel
/**
 * @author ShiZhan
 * adapter interface
 */
trait Modeler {
  def usage: String
  def core: OntModel
  def translate(name: String): OntModel
}

object Modelers {
  val modelerMap: Map[String, Modeler] = Map(
    "directory" -> Directory)
  def getModeler(t: String) = modelerMap.getOrElse(t, Unknown)
  def getModelerList =
    modelerMap.flatMap {
      case (s, a) => List("  %s: \t %s".format(s, a.usage))
    }.mkString("\n")
}