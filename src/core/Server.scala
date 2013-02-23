/**
 *
 */
package core

import scala.actors.Actor
import scala.actors.Actor._
import scala.actors.remote.RemoteActor._

import model.Model.queryStore
import util.Logging

/**
 * @author ShiZhan
 * TriGraM Server
 */

class Server(address: Array[String]) extends Actor with Logging {

  private val port = address(1).toInt

  def act() {
    alive(port)
    register('TrigramService, self)

    loop {
      receive {
        case Query(q) =>
          reply(QueryResult(queryStore(q)))
        case QuitOp(reason) =>
          logger.info("Client [%s] quit for [%s]".format(sender, reason))

          reply(QuitConfirm())
        case _ =>
          reply(QueryResult("Not supported"))
      }
    }
  }

  def run = {
    logger.info("Starting server on " + address.mkString(":"))

    start
  }

}
