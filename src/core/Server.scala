/**
 *
 */
package core

import scala.actors.Actor
import scala.actors.Actor._
import scala.actors.remote.RemoteActor
import scala.actors.remote.RemoteActor._

import util.Logging

/**
 * @author ShiZhan
 * Server loop
 */

class ServerSink(port: Int) extends Actor {

  RemoteActor.classLoader = getClass().getClassLoader()

  def act() {
    alive(port)
    register('TrigramService, self)

    while (true) {
      receive {
        case msg => {
          println(msg)
          reply("Thanks: " + msg)
        }
      }
    }
  }

}

object Server extends Logging {

  def run(address: Array[String]): Unit = {
    logger.info("Starting server on " + address.mkString(":"))

    (new ServerSink(address(1).toInt)).start()
  }

}
