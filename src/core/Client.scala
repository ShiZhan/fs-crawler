/**
 *
 */
package core

import scala.actors.Actor
import scala.actors.remote.RemoteActor._
import scala.actors.remote.Node

import util.Logging

/**
 * @author ShiZhan
 * 2013
 * Client classes and APIs
 */

class RemoteTrigramActor(n: Node) extends Actor with Logging {

  private val remoteActor = select(n, 'TrigramService)

  def act = { /* must implement */ }

  def deliver(op: TOperation): String = {
    remoteActor !? op match {
      case QueryResult(result) => return result
      case _ => return "unexpected deliver reply"
    }
  }

  def close(reason: String): Unit = {
    remoteActor !? QuitOp(reason) match {
      case QuitConfirm() => exit
      case _ => throw new Exception("unexpected quit reply")
    }
  }

}

class Connection(address: Array[String]) {

  private val port = address(1).toInt
  private val node = Node(address(0), port)
  private val trigramActor = new RemoteTrigramActor(node)
  trigramActor.start

  def doQuery(q: String): String = return trigramActor.deliver(Query(q))

  def doQuit(reason: String) = trigramActor.close(reason)

}
