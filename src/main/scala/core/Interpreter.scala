/**
 * command interpreter
 */
package core

/**
 * @author ShiZhan
 * translate domain specific command into SPARQL query
 */
object Interpreter {

  type Parser = String => String
  type ParserMap = Map[String, Parser]
  private val pasers: ParserMap = Map(
    "q" -> parseSparql,
    "p" -> parsePosix,
    "r" -> parseRest)

  def parseSparql(cmd: String) = cmd

  def parsePosix(cmd: String) = cmd

  def parseRest(cmd: String) = cmd

  def parseUnknown(cmd: String) = "No valid interpreter is associated\n" +
    "Available interpreters: " +
    pasers.flatMap { case (k, v) => List(k) }.mkString("[", "] [", "]")

  def get(prefix: String): Parser = pasers.getOrElse(prefix, parseUnknown)

  private val help = Map(
    "q" -> "SPARQL query",
    "p" -> "POSIX operation",
    "r" -> "REST operation")

  def printHelp = help.foreach { case (k, v) => println("%s -> %s".format(k, v)) }

}