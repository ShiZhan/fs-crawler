/**
 * Adapter Interface
 */
package modeler

import com.hp.hpl.jena.rdf.model.Model

/**
 * @author ShiZhan
 * adapter interface
 */
trait Modeler {
  def usage: String
  def core: Model
  def translate(resource: String): Model
}

/*
 * for adding more modelers:
 * 1. extends from Modeler and implement all 3 methods
 *    usage:     help information
 *    core:      TBOX for modeling this category
 *    translate: ABOX for modeling this category
 * 2. add vocabulary (if needed) to TGM.scala
 * 3. add statements (if needed) to core method
 */
object Modelers {

  val modelerMap: Map[String, Modeler] = Map(
    "directory" -> Directory)

  def getModeler(t: String) = modelerMap.getOrElse(t, Unknown)

  def getModelerHelp =
    modelerMap.flatMap {
      case (s, a) => List("  %s: \t %s".format(s, a.usage))
    }.mkString("\n")

  def getCoreModel = modelerMap.foldLeft(Unknown.core)((r, c) => r.union(c._2.core))

}