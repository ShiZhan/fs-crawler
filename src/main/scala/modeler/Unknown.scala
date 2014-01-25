/**
 * Modeler for unknown resources
 */
package modeler

/**
 * @author ShiZhan
 * Modeler for unknown resources, modeler map default.
 */
object Unknown extends Modeler with helper.Logging {
  val key = "unknown"
  val usage = null
  def run(o: List[String]) = logger.info("Modeler type unkown")
}