/**
 * Modeler Interface
 */
package modeler

/**
 * @author ShiZhan
 * interface for modelers, for adding more, follow these steps:
 * 1. extends from Modeler trait and assign (override) a unique "key"
 * 2. add help information by overriding "usage"
 * 3. implement "tBox" and "aBox" method with [optional] additional vocabulary
 *
 *    key:       unique identifier
 *    usage:     help information
 *    run:      build ABOX for modeling this category
 *
 *    then add this modeler into modelerMap
 */
trait Modeler {
  val key: String = "base"
  val usage: String = null
  def run(input: String, output: String): Unit
}

/**
 * managing modelers for translating various resources to RDF model
 */
object Modelers {

  private val modelerMap = List(
    CimSchema,
    CimSchemaEx,
    Directory,
    DirectoryEx,
    Archive)
    .map(m => (m.key -> m)) toMap

  def getHelp =
    modelerMap.map { case (k, m) => "  " + k + ":   \t" + m.usage }.mkString("\n")

  def run(k: String, i: String, o: String) =
    modelerMap.getOrElse(k, Unknown).run(i, o)

}