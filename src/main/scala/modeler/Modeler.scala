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
  val adapterMap: Map[String, Modeler] = Map(
    "directory" -> Directory)
  def getModeler(t: String) = adapterMap.getOrElse(t, Unknown)
  def getModelerList =
    adapterMap.flatMap {
      case (s, a) => List("  %s: \t %s".format(s, a.usage))
    }.mkString("\n")
}