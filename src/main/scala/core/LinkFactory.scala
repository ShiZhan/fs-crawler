/**
 *
 */
package core

import akka.actor.{ Actor, ActorRef, ActorSystem }
import akka.actor.ActorDSL._
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigFactory.parseString

/**
 * @author ShiZhan
 * 2013
 * uniform actor system
 */
trait TRequest

case class Request(req: String) extends TRequest

trait TResponse

case class Response(rsp: String) extends TResponse

object LinkFactory {

  private val configTemplate =
    """
akka {
  actor {
    provider = "akka.remote.RemoteActorRefProvider"
  }

  remote {
    netty.tcp {
      port = %s
    }
  }

  loglevel = ERROR
}
"""

  def createActorSystem(name: String, port: String): ActorSystem =
    ActorSystem(name,
      ConfigFactory.load(parseString(configTemplate.format(port))))

  def createLocal(system: ActorSystem,
    name: String, handler: String => String): ActorRef =
    actor(system, name)(new Act {
      become {
        case Request(req) =>
          sender ! Response(handler(req))
      }
    })

  private val akkaURLTemplate = "akka.tcp://%s@%s:%s/user/%s"

  def createRemote(system: ActorSystem,
    systemName: String, ip: String, port: String, name: String): ActorRef =
    system.actorFor(akkaURLTemplate.format(systemName, ip, port, name))

}
