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

class Connection(address: Array[String]) extends Actor with Logging {

  private val port = address(1).toInt
  private val node = Node(address(0), port)
  private val remoteActor = select(node, 'TrigramService)
//  trigramActor.start

  def act = { /* must implement */ }

  def doQuery(q: String): String = {
    remoteActor !? Query(q) match {
      case QueryResult(result) => return result
      case _ => return "unexpected deliver reply"
    }
  }

  def doQuit(reason: String): Unit = {
    remoteActor !? QuitOp(reason) match {
      case QuitConfirm() => exit
      case _ => throw new Exception("unexpected quit reply")
    }
  }

}
