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

class TrigramActor(port: Int) extends Actor with Logging {

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

}

class Server(address: Array[String]) extends Logging {

  def run = {
    logger.info("Starting server on " + address.mkString(":"))

    (new TrigramActor(address(1).toInt)).start()
  }

}
