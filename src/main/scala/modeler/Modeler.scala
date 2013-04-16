/**
 * Modeler Interface
 */
package modeler

import com.hp.hpl.jena.rdf.model.Model

/**
 * @author ShiZhan
 * interface for managing modelers, for adding more, follow these steps:
 * 1. PREPARATION:
 *    add vocabulary (if needed) to TGM.scala
 * 2. BUILD:
 *    extends from Modeler trait and implement all 3 methods
 *    usage:     help information
 *    core:      build TBOX for modeling this category
 *    translate: build ABOX for modeling this category
 *    then add this modeler into modelerMap
 */
trait Modeler {
  def usage: String
  def core: Model
  def translate(resource: String): Model
}

object Modelers {

  private val modelerMap: Map[String, Modeler] = Map(
    "directory" -> Directory)

  def getModeler(t: String) = modelerMap.getOrElse(t, Unknown)

  def getModelerHelp =
    modelerMap.map { case (s, a) => "  %s: \t %s".format(s, a.usage) }.mkString("\n")

  def getCoreModel = modelerMap.foldLeft(Unknown.core)((r, c) => r.union(c._2.core))

}