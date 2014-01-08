/**
 * Modeler for unknown resources
 */
package modeler

import helper.Logging

/**
 * @author ShiZhan
 * providing empty core model and translated model,
 * also function as default object for modeler map
 */
object Unknown extends Modeler with Logging {
  def run(o: Array[String]) = logger.info("Modeler type unkown")
}