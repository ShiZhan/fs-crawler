/**
 * Modeler for unknown resources
 */
package modeler

import com.hp.hpl.jena.rdf.model.ModelFactory
import util.Logging

/**
 * @author ShiZhan
 * unknown resource modeler
 * providing empty core model and translated model
 * also function as default object for modeler map
 * and dummy model for merging core models
 */
object Unknown extends Modeler with Logging {

  def usage = null

  def core = ModelFactory.createDefaultModel

  def translate(n: String) = {
    logger.info("unkown resource: " + n)

    ModelFactory.createDefaultModel
  }

}