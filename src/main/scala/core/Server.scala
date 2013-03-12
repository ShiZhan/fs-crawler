/**
 * TriGraM Server
 */
package core

import DataFactory.queryDataset
import LinkFactory.{createActorSystem, createLocal}
import util.Logging

/**
 * @author ShiZhan
 * 2013
 * Server singleton
 */
object Server extends Logging {

  def run(port: String) = {
    logger.info("Starting server on port: " + port)

    val serviceActor = createLocal(
      createActorSystem("TrigramServer", port), "Server",
      queryDataset)
  }

}
