/**
 * Modeler Interface
 */
package modeler

import com.hp.hpl.jena.rdf.model.{ ModelFactory, Model }
import util.Logging

/**
 * @author ShiZhan
 * interface for modelers, for adding more, follow these steps:
 * 1. extends from Modeler trait and assign (override) a unique "key"
 * 2. [optional] add vocabulary
 * 3. [optional] generate core model/ABOX by overriding "core" method
 * 4. [optional] add help information by overriding "usage"
 * 5. implement "translate" method
 * 
 *    key:       unique identifier
 *    usage:     help information
 *    core:      build TBOX for modeling this category
 *    translate: build ABOX for modeling this category
 *    then add this modeler into modelerMap
 */
trait Modeler {
  val key: String = "BaseModeler"
  val usage: String = null
  def core: Model = ModelFactory.createDefaultModel
  def translate(input: String, output: String): Unit
}

/**
 * managing modelers for translating various resources to RDF model
 */
object Modelers extends Logging {

  private val modelerMap: Map[String, Modeler] = Map(
    Directory.key -> Directory,
    DirectoryEx.key -> DirectoryEx)

  def getModel(t: String, i: String, o: String) =
    modelerMap.getOrElse(t, Unknown).translate(i, o)

  def getHelp =
    modelerMap.map { case (s, a) => "  %s: \t %s".format(s, a.usage) }.mkString("\n")

  def getCoreModel = {
    val m = modelerMap.foldLeft(Unknown.core)((r, c) => r.union(c._2.core))
    m.write(new java.io.FileOutputStream(TGM.local), "RDF/XML-ABBREV")

    logger.info("[%d] triples saved to core model file [%s]".format(m.size, TGM.local))
  }

}