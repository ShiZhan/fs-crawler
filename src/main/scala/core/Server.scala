/**
 * TriGraM Server
 */
package core

import ModelFactory.{ loadStore, queryStore }
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

    val model = loadStore

    val serviceActor = createLocal(
      createActorSystem("TrigramServer", address(1)), "Server",
      queryStore(model, _))

    model.close
  }

}
