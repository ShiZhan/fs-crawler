/**
 *
 */
package core

import scala.actors.Actor
import scala.actors.Actor._
import scala.actors.remote.RemoteActor._

import util.Logging

/**
 * @author ShiZhan
 *
 */

case object Ping
case object Pong
case object Quit

class RemotePong(port: Int) extends Actor {

  def act() {
    alive(port)
    register('Pong, self)

    while (true) {
      receive {
        case Ping =>
          println("Pong: ping")
          sender ! Pong
        case Quit =>
          println("Pong: stop")
          exit()
      }
    }
  }

}

object Server extends Logging {

  def run(address: Array[String]): Unit = {
    logger.info("Starting server on " + address.mkString(":"))

    val pong = new RemotePong(address(1).toInt)

    pong.start()
  }

}