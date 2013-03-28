/**
 * command handler
 */
package core

/**
 * @author ShiZhan
 * Domain Specific Command handlers
 * Store trait wrapper
 */
trait Handler extends Store {

  type Handler = (String => Unit, String)
  type HandlerMap = Map[String, Handler]
  private val handlerMap: HandlerMap = Map(
    "QUERY" -> (handlerQuery, "SPARQL query interpreter"),
    "UPDATE" -> (handlerUpdate, "SPARQL update interpreter"),
    "POSIX" -> (handlerPosix, "perform POSIX-like operation"),
    "REST" -> (handlerRest, "perform RESTful operation"))

  val inputNotice = "NOTICE: use . in a new line to submit"
  private def readMore: String = {
    val line = io.Source.stdin.getLines.next
    line match {
      case "." => ""
      case _ => line + "\n" + readMore
    }
  }

  def handlerQuery(prompt: String): Unit = {
    println(inputNotice)

    print(prompt)

    for (input <- io.Source.stdin.getLines) {
      val sparql = input match {
        case "exit" => return
        case _ => input + "\n" + readMore
      }

      try {
        val result = sparqlQuery(sparql)
        println("SPARQL Query: " + sparql + "\nReqult: " + result)
      } catch {
        case e: Exception =>
          println("SPARQL Query exception:")
          println(e)
      }

      print(prompt)
    }
  }

  def handlerUpdate(prompt: String): Unit = {
    println(inputNotice)

    print(prompt)

    for (input <- io.Source.stdin.getLines) {
      val sparql = input match {
        case "exit" => return
        case _ => input + "\n" + readMore
      }

      try {
        sparqlUpdate(sparql)
        println("SPARQL Update: " + sparql + "\nExecuted normally")
      } catch {
        case e: Exception =>
          println("SPARQL Query exception:")
          println(e)
      }

      print(prompt)
    }
  }

  def handlerPosix(prompt: String): Unit = {
    print(prompt)

    for (input <- io.Source.stdin.getLines) {
      val output = input.split(" ").toList match {
        case "exit" :: Nil => return
        case "ls" :: item :: Nil => "Content of: " + item
        case "stat" :: item :: Nil => "Properties of: " + item
        case "cp" :: from :: to :: Nil => "Copy object from [%s] to [%s]".format(from, to)
        case "mv" :: from :: to :: Nil => "Move object from [%s] to [%s]".format(from, to)
        case "rm" :: item :: Nil => "Delete: " + item
        case "mkdir" :: item :: Nil => "Delete: " + item
        case "" :: Nil => ""
        case _ => "Unknown POSIX command: " + input
      }
      println(output)

      print(prompt)
    }
  }

  def handlerRest(prompt: String) = println("Work in progress")

  def handlerUnknown(prompt: String) = println("No valid command handler is associated\n" +
    "Available handlers: " +
    handlerMap.flatMap { case (k, v) => List(k) }.mkString("[", "] [", "]"))

  def enterDSCLI(mode: String) =
    handlerMap.getOrElse(mode, (handlerUnknown _, "")) match { case (h, s) => h(mode + " > ") }

  val help = handlerMap.flatMap {
    case (m, (h, s)) => List("  %s:\t%s".format(m, s))
  }.mkString("\n")

}
