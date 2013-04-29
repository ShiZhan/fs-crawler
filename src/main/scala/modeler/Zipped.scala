/**
 * Modeler for compressed resources
 */
package modeler

import util.Logging

/**
 * @author ShiZhan
 * providing empty core model and translated model, also function as default
 * object for modeler map and dummy model for merging core models
 */
object Zipped extends Modeler with Logging {

  override val key = "zip"

  override val usage = "Translate zipped file contents"

  def tBox = logger.info("Model zipped files")

  def aBox(i: String, o: String) = logger.info("Model zipped files")

}