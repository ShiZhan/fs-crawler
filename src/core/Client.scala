/**
 * Client classes and APIs
 */
package core

import akka.actor.{ ActorRef, ActorSystem, Props }
import akka.actor.ActorDSL._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigFactory.parseString

import util.Logging

/**
 * @author ShiZhan
 * 2013
 * Client singleton
 */

object Client extends Logging {

  private val clientTemplate =
    """
akka {
  actor {
    provider = "akka.remote.RemoteActorRefProvider"
  }

  remote {
    netty.tcp {
      hostname = "localhost"
      port = 0
    }
  }

  loglevel = ERROR
} 
"""

  private implicit val timeout = Timeout(10000)
  private val config = ConfigFactory.load(parseString(clientTemplate))
  private val system = ActorSystem("TrigramClient", config)

  def shutdown = system.shutdown

  class Connect(address: Array[String]) {

    private val serverURL = "akka://TrigramServer@%s:%s/user/Server".
      format(address(0), address(1))
    private val server = system.actorFor(serverURL)

    def deliver(q: String): String = {
      Await.result(server ? Query(q), Duration.Inf) match {
        case QueryResult(result) => return result
        case _ => return "Unknown Result"
      }
    }
    
    def close(reason: String): Unit = {
      Await.result(server ? QuitOp(reason), Duration.Inf) match {
        case QuitConfirm => return
        case _ => throw new Exception("close error")
      }
    }
  }

}
