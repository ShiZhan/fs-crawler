/**
 * Modeler for unknown resources
 */
package modeler

/**
 * @author ShiZhan
 * Modeler for unknown resources, modeler map default.
 */
object Unknown extends Modeler with helper.Logging {
  def run(o: Array[String]) = logger.info("Modeler type unkown")
}