/**
 * Modeler Interface
 */
package modeler

/**
 * @author ShiZhan
 * interface template for modelers, for adding more, follow these steps:
 * 1. use Modeler trait to create the new modeler
 *   a. extends from Modeler trait and assign (override) a unique "key"
 *   b. add help information by overriding "usage"
 *   c. implement "run" method with modeler dedicated options
 * 2. add this modeler into "Modelers.ms"
 */
trait Modeler {
  val key: String = "base"
  val usage: String = null
  def run(options: Array[String]): Unit
}

/**
 * modeler hub
 */
object Modelers {
  private val ms = Seq(
    CimSchema,
    CimSchemaEx,
    Directory,
    Archive,
    Checksum)

  val help = ms.map { m => "  %s:  \t%s".format(m.key, m.usage) }.mkString("\n")

  def run(k: String, o: Array[String]) = ms.find(_.key == k).getOrElse(Unknown).run(o)
}