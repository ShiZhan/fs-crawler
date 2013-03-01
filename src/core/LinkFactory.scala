/**
 *
 */
package core

import akka.actor.{ Actor, ActorRef, ActorSystem }
import akka.actor.ActorDSL._
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigFactory.parseString

import util.Logging

/**
 * @author ShiZhan
 * 2013
 * uniform actor system
 */
trait TRequest

case class Request(req: String) extends TRequest

trait TResponse

case class Response(rsp: String) extends TResponse

object LinkFactory extends Logging {

  private val configTemplate =
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

  loglevel = ERROR
}
"""

  def createActorSystem(port: String): ActorSystem = {
    logger.info("starting actor system on port: " + port)

    val config = ConfigFactory.load(parseString(configTemplate.format(port)))
    return ActorSystem("TrigramServer", config)
  }

  def createLocal(system: ActorSystem, name: String): ActorRef = {
      return actor(system, name)(new Act {
          become {
            case Request("Hello") =>
              sender ! Response("World!")
            case _ =>
              sender ! Response("Not supported")
          }
        })
  }

  def createRemote(system: ActorSystem, ip: String, port: String, name: String): ActorRef =
    return system.actorFor("akka://TrigramServer@%s:%s/user/%s".
      format(ip, port, name))

}