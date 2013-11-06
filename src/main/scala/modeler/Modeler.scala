/**
 * Modeler Interface
 */
package modeler

/**
 * @author ShiZhan
 * interface for modelers, for adding more, follow these steps:
 * 1. extends from Modeler trait and assign (override) a unique "key"
 * 2. add help information by overriding "usage"
 * 3. implement "run" method with (in, out)
 *
 *    key:       unique identifier
 *    usage:     help information
 *    run:       build model
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

  private val ms = Seq(
    CimSchema,
    CimSchemaEx,
    Directory,
    DirectoryEx,
    Archive,
    Checksum)

  def getHelp =
    ms.map { m => "  %s:  \t%s".format(m.key, m.usage) }.mkString("\n")

  def run(k: String, i: String, o: String) =
    ms.find(_.key == k).getOrElse(Unknown).run(i, o)

}