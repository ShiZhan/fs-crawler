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
 *   c. update list of CIM classes that used in this modeler
 *   d. implement "run" method with modeler dedicated options
 * 2. add this modeler into "Modelers.ms"
 */
trait Modeler {
  val key: String
  val usage: String
  val tbox: Seq[String]
  def run(options: List[String]): Unit
}

/**
 * modeler hub
 */
object Modelers {
  private val ms = Seq(
    Directory,
    Archive,
    Checksum)

  val help = ms.map { m => "  %s:  %s".format(m.key, m.usage) } mkString("\n")
  val tbox = ms.flatMap(_.tbox).distinct

  def run(k: String, o: List[String]) = ms.find(_.key == k).getOrElse(Unknown).run(o)
}