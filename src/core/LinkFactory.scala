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
    
  private val akkaURLTemplate = "akka://TrigramServer@%s:%s/user/%s"

  def createActorSystem(port: String): ActorSystem = {
    logger.info("starting actor system on port: " + port)

    val config = ConfigFactory.load(parseString(configTemplate.format(port)))
    return ActorSystem("TrigramServer", config)
  }

  def createLocal(system: ActorSystem,
    name: String, handler: String => String): ActorRef = {
    return actor(system, name)(new Act {
        become {
          case Request(req) =>
            sender ! Response(handler(req))
          case _ =>
            sender ! Response("Unhandled request!")
        }
      })
  }

  def createRemote(system: ActorSystem,
    ip: String, port: String, name: String): ActorRef =
    return system.actorFor(akkaURLTemplate.format(ip, port, name))

}
