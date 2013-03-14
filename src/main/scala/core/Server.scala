/**
 * TriGraM Server
 */
package core

import akka.actor.ActorDSL._

import LinkFactory._
import DataFactory.queryDataset
import util.Logging

/**
 * @author ShiZhan
 * 2013
 * Server singleton
 */
object Server extends Logging {

  def run(port: String) = {

    logger.info("Starting server on port: " + port)

    val system = createActorSystem("TrigramServer", port)

    val serviceActor = actor(system, "Server")(new Act {
      become {
        case Request(req) =>
          sender ! Response(queryDataset(req))
        case _ =>
          sender ! Response("Unhandled request!")
      }
    })
      
  }

}
