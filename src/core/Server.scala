/**
 * TriGraM Server
 */
package core

import akka.actor.{ Props, Actor, ActorSystem }
import akka.actor.ActorDSL._
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigFactory.parseString

import model.Model.queryStore
import util.Logging

/**
 * @author ShiZhan
 * 2013
 * Server singleton
 */

object Server extends Logging {

  private val serverTemplate =
    """
akka {
  actor {
    provider = "akka.remote.RemoteActorRefProvider"
  }

  remote {
    netty {
      port = %s
    }
  }

  loglevel = INFO
}
"""

  def run(address: Array[String]) = {
    logger.info("Starting server on " + address.mkString(":"))

    val config = ConfigFactory.load(parseString(serverTemplate.format(address(1))))
    val system = ActorSystem("TrigramServer", config)
    val serverActor = actor(system, "Server")(new Act {
      become {
        case Query(q) =>
          sender ! QueryResult(queryStore(q))
        case QuitOp(reason) =>
          sender ! QuitConfirm
          logger.info("Client [%s] quit for [%s]".format(sender, reason))
        case _ =>
          sender ! QueryResult("Not supported")
      }
    })
  }

}
