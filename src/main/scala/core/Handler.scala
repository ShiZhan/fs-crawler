/**
 * command handler
 */
package core

import scala.io.Source
import java.io.File

/**
 * @author ShiZhan
 * domain specific command handlers
 * Store trait wrapper
 */
trait Handler extends Store {

  type Handler = String => Any
  type HandlerMap = Map[String, Handler]
  private val handlerMap: HandlerMap = Map(
    "q" -> handlerSparql,
    "p" -> handlerPosix,
    "r" -> handlerRest)

  def handlerSparql(sparqlFile: String) = {
    val f = new File(sparqlFile)
    if(f.exists()) {
      val sparql = Source.fromFile(sparqlFile).getLines.mkString("\n")
      "SPARQL: " + sparql + "\nReqult: " + querySelect(sparql)
    }
    else {
      "SPARQL file %s not exist".format(sparqlFile)
    }
  }

  def handlerPosix(cmd: String) = cmd.split(" ").toList match {
    case "ls" :: item :: Nil => "Content of: " + item
    case "stat" :: item :: Nil => "Properties of: " + item
    case "cp" :: from :: to :: Nil => "Copy object from [%s] to [%s]".format(from, to)
    case "mv" :: from :: to :: Nil => "Move object from [%s] to [%s]".format(from, to)
    case "rm" :: item :: Nil => "Delete: " + item
    case "mkdir" :: item :: Nil => "Delete: " + item
    case _ => "Unknown POSIX command: " + cmd
  }

  def handlerRest(cmd: String) = "Work in progress"

  def handlerUnknown(cmd: String) = "No valid command handler is associated\n" +
    "Available handlers: " +
    handlerMap.flatMap { case (k, v) => List(k) }.mkString("[", "] [", "]")

  def getHandler(key: String): Handler = handlerMap.getOrElse(key, handlerUnknown)

  private val handlerHelp = Map(
    "q" -> "load and execute SPARQL query file",
    "p" -> "perform POSIX-like operation",
    "r" -> "perform RESTful operation")

  val help = handlerHelp.flatMap { case (k, v) => List(k + ": " + v) }.mkString("\n")

}