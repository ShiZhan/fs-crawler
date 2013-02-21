/**
 *
 */
package core

import scala.actors.Actor
import scala.actors.remote.RemoteActor._
import scala.actors.remote.Node

/**
 * @author ShiZhan
 * 2013
 * Client classes and APIs
 */

class RemoteTrigramActor(n: Node) extends Actor {

  private val remoteActor = select(n, 'TrigramService)

  def act = {/* must implement */}

  def deliver(msg: String): String = {
    remoteActor !? Query(msg) match {
      case QueryResult(result) => return result
      case _ => return "unexpected result"
    }
  }

}

class Connection(address: Array[String]) {

  private val port = address(1).toInt
  private val node = Node(address(0), port)
  private val trigramActor = new RemoteTrigramActor(node)
  trigramActor.start

  def doQuery(query: String): String = return trigramActor.deliver(query)

}
