/**
 *
 */
package modeler

import com.hp.hpl.jena.rdf.model.ModelFactory
import util.Logging
/**
 * @author ShiZhan
 *
 */
object Unknown extends Modeler with Logging {

  private val help = null

  def usage = { help }

  def core = ModelFactory.createDefaultModel

  def translate(n: String) = {
    logger.info("unkown resource: " + n)

    ModelFactory.createOntologyModel
  }

}