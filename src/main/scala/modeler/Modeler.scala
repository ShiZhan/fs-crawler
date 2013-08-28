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

  private val modelerMap: Map[String, Modeler] = Map(
    Directory.key -> Directory,
    DirectoryEx.key -> DirectoryEx,
    CimSchema.key -> CimSchema,
    CimSchemaEx.key -> CimSchemaEx,
    Archive.key -> Archive)

  def getHelp =
    modelerMap.map { case (s, m) => "  " + s + ":   \t" + m.usage }.mkString("\n")

  def run(t: String, i: String, o: String) =
    modelerMap.getOrElse(t, Unknown).run(i, o)

}