/**
 * TriGraM Server
 */
package core

import ModelFactory.{ loadDataset, queryDataset }
import LinkFactory._
import util.Logging

/**
 * @author ShiZhan
 * 2013
 * Server singleton
 */
object Server extends Logging {

  def run(address: Array[String]) = {
    logger.info("Starting server on port: " + address(1))

    val model = loadDataset

    val serviceActor = createLocal(
      createActorSystem("TrigramServer", address(1)), "Server",
      queryDataset(model, _))

    model.close
  }

}
