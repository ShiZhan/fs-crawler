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
 * TriGraM Server
 */

class TrigramActor(port: Int) extends Actor with Logging {

  def act() {
    alive(port)
    register('TrigramService, self)

    loop {
      receive {
        case Query(q) =>
          reply(QueryResult("Result: " + q))
        case QuitOp(reason) =>
          logger.info("Client [%s] quit for [%s]".format(sender, reason))

          reply(QuitConfirm())
        case _ =>
          reply(QueryResult("Not supported"))
      }
    }
  }

}

object Server extends Logging {

  def run(address: Array[String]): Unit = {
    logger.info("Starting server on " + address.mkString(":"))

    (new TrigramActor(address(1).toInt)).start()
  }

}
