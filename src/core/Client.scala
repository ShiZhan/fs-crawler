/**
 *
 */
package core

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }
import akka.actor.ActorDSL._
import akka.pattern.ask
import scala.concurrent.Await
import akka.util.Timeout
import scala.concurrent.duration.Duration
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigFactory.parseString

import util.Logging

/**
 * @author ShiZhan
 * 2013
 * Client classes and APIs
 */

object Client {

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
  private val client = actor(system, "Client")(new Act {
    become {
      case (actor: ActorRef, op: TOperation) =>
        Await.result(actor ? op, Duration.Inf) match {
          case QueryResult(result) => sender ! QueryResult(result)
          case _ => sender ! QueryResult("Unknown Result")
        }

      case QuitOp(reason) =>
        context.stop(self)
    }
  })

  def shutdown(reason: String) = {
    client ! QuitOp(reason)
    system.shutdown
  }

  class Connect(address: Array[String]) {
    private val serverURL = "akka://TrigramServer@%s:%s/user/Server".
                            format(address(0), address(1))
    private val server = system.actorFor(serverURL)

    def doQuery(q: String): String = {
      Await.result(client ? (server, Query(q)), Duration.Inf) match {
        case QueryResult(result) => return result
        case _ => return "Unknown Result"
      }
    }
  }

}
