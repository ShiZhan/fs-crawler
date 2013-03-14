/**
 * Client classes and APIs
 */
package core

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.duration._

import LinkFactory._

/**
 * @author ShiZhan
 * 2013
 * Client object
 */
object Client {

  private val system = createActorSystem("TrigramClient", "0")

  def shutdown = system.shutdown

  class Connection(address: Array[String]) {
    private val remoteActor = system.actorFor(
      akkaURLTemplate.format("TrigramServer", address(0), address(1), "Server"))

    private implicit val timeout = Timeout(2 seconds)

    def deliver(q: String): String =
      Await.result(remoteActor ? Request(q), Duration.Inf) match {
        case Response(result) => return result
        case _ => return "Unknown Result"
      }

  }

  def getConnection(a: Array[String]) = new Connection(a)

}
