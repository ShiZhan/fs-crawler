/**
 * Modeler for unknown resources
 */
package modeler

import util.Logging

/**
 * @author ShiZhan
 * providing empty core model and translated model, also function as default
 * object for modeler map and dummy model for merging core models
 */
object Unknown extends Modeler with Logging {

  def aBox(i: String, o: String) = logger.info("unkown resource: " + i)

}