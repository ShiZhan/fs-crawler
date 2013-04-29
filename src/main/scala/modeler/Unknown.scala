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

  def tBox = logger.info("TBox type unkown")

  def aBox(i: String, o: String) = logger.info("ABox type unkown")

}