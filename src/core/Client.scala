/**
 *
 */
package core

import scala.actors.Actor
import scala.actors.remote.RemoteActor
import scala.actors.remote.RemoteActor._
import scala.actors.remote.Node

import util.Logging

/**
 * @author ShiZhan
 * 2013
 * Client classes and APIs
 */

class ServerSource(peer: Node) extends Actor {

  def act() {
    RemoteActor.classLoader = getClass().getClassLoader()
  }

  def send(msg: String): String = {
    val sink = select(peer, 'TrigramService)
//    link(sink)
    sink !? msg match {
      case response => return "Server's response is [" + response + "]"
    }
  }

}

class Connection(address: Array[String]) extends Logging {

  val remotePort = address(1).toInt
  val remoteNode = Node(address(0), remotePort)
  val serverSource = new ServerSource(remoteNode)

  serverSource.start()

  def doQuery(queryString: String): String = {
    logger.info("enter doQuery")

    return serverSource.send(queryString)
  }
}
