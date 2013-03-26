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

  def parsePosix(cmd: String) = cmd.split(" ").toList match {
    case "ls" :: item :: Nil => "Content of: " + item
    case "stat" :: item :: Nil => "Properties of: " + item
    case "cp" :: from :: to :: Nil => "Copy object from [%s] to [%s]".format(from, to)
    case "mv" :: from :: to :: Nil => "Move object from [%s] to [%s]".format(from, to)
    case "rm" :: item :: Nil => "Delete: " + item
    case "mkdir" :: item :: Nil => "Delete: " + item
    case _ => "Unknown POSIX command: " + cmd
  }

  def parseRest(cmd: String) = "Work in progress"

  def parseUnknown(cmd: String) = "No valid interpreter is associated\n" +
    "Available interpreters: " +
    pasers.flatMap { case (k, v) => List(k) }.mkString("[", "] [", "]")

  def get(prefix: String): Parser = pasers.getOrElse(prefix, parseUnknown)

  private val parserHelp = Map(
    "q" -> "SPARQL query",
    "p" -> "POSIX operation",
    "r" -> "REST operation")

  val help = parserHelp.flatMap { case (k, v) => List(k + ": " + v) }.mkString("\n")

}