/**
 * Client classes and APIs
 */
package core

import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.duration.Duration

import LinkFactory._
import util.Logging

/**
 * @author ShiZhan
 * 2013
 * Client singleton
 */

object Client {

  private val system = createActorSystem("TrigramClient", "0")

  def shutdown = system.shutdown

  class Connect(address: Array[String]) {

    private val remoteActor = createRemote(system,
      "TrigramServer", address(0), address(1), "Server")

    private implicit val timeout = Timeout(10000)

    def deliver(q: String): String = {
      Await.result(remoteActor ? Request(q), Duration.Inf) match {
        case Response(result) => return result
        case _ => return "Unknown Result"
      }
    }

  }

}
