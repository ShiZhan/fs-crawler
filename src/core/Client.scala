/**
 *
 */
package core

import scala.actors.Actor
import scala.actors.remote.RemoteActor
import scala.actors.remote.RemoteActor._
import scala.actors.remote.Node

/**
 * @author ShiZhan
 * 2013
 * Client classes and APIs
 */

class ServerActor(peer: Node) extends Actor {

  private val remoteActor = select(peer, 'TrigramService)

  def act() = {/* must implement here */}

  def deliver(msg: String): String = remoteActor !? msg match {
    case response => return "Server's response is [" + response + "]"
  }

  def stop() = exit

}

class Connection(address: Array[String]) {

  private val serverActor = new ServerActor(Node(address(0), address(1).toInt))

  def doQuery(queryString: String): String =
    return serverActor.deliver(queryString)

  def close() = serverActor.stop

}
