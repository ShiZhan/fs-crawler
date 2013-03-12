/**
 * TriGraM Server
 */
package core

import DataFactory.{ initDataset, queryDataset }
import LinkFactory._
import util.Logging

/**
 * @author ShiZhan
 * 2013
 * Server singleton
 */
object Server extends Logging {

  def run(port: String) = {
    logger.info("Starting server on port: " + port)

    val model = initDataset()

    val serviceActor = createLocal(
      createActorSystem("TrigramServer", port), "Server",
      queryDataset(model, _))

    model.close
  }

}
