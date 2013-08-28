/**
 * Modeler for unknown resources
 */
package modeler

import util.Logging

/**
 * @author ShiZhan
 * providing empty core model and translated model,
 * also function as default object for modeler map
 */
object Unknown extends Modeler with Logging {

  def run(i: String, o: String) = logger.info("Modeler type unkown")

}