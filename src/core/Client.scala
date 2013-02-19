/**
 *
 */
package core

import scala.actors.Actor
import scala.actors.Actor._
import scala.actors.Exit
import scala.actors.remote.RemoteActor._
import scala.actors.remote.Node

import util.Logging

/**
 * @author ShiZhan
 * 2013
 * Client classes and APIs
 */

class RemotePing(port: Int, peer: Node, count: Int) extends Actor {
  trapExit = true

  def act() {
    alive(port)
    register('Ping, self)
    
    val pong = select(peer, 'Pong)
    link(pong)
    
    var pingsLeft = count - 1
    pong ! Ping
    while (true) {
      receive {
        case Pong =>
          println("Ping: pong")
          if (pingsLeft > 0) {
            pong ! Ping
            pingsLeft -= 1
          } else {
            println("Ping: start termination")
            pong ! Quit
          }
        case Exit(pong, 'normal) =>
            println("Ping: stop")
            exit()
      }
    }
  }

}

class Connection(address: Array[String]) extends Logging {

  val port = address(1).toInt
  val remoteNode = Node(address(0), port)
  val ping = new RemotePing(port, remoteNode, 16)

  def test() = {}
}