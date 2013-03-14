/**
 * Actor system encapsulation
 */
package core

import akka.actor.ActorSystem
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

  val akkaURLTemplate = "akka.tcp://%s@%s:%s/user/%s"

}
