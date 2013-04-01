/**
 * command handler
 */
package core

/**
 * @author ShiZhan
 * extents Store class with Domain Specific Command handlers
 * for use in Console application
 */
class Handler(location: String) extends Store(location) {

  type Handler = String => Unit
  type HandlerMap = Map[String, (Handler, String)]
  private val handlerMap: HandlerMap = Map(
    "query" -> (handlerQuery, "SPARQL query interpreter"),
    "update" -> (handlerUpdate, "SPARQL update interpreter"),
    "rest" -> (handlerRest, "perform RESTful operation"))

  private def readSPARQL = {
    print("SPARQL input, end with Ctrl+E <- ")
    io.Source.fromInputStream(System.in).takeWhile(_ != 5.toChar).mkString
  }

  val sparqlConsoleHelp =
    "Use [exit] to go back, press [enter] to begin SPARQL input."

  def handlerQuery(prompt: String): Unit = {
    println(sparqlConsoleHelp)
    print(prompt)

    for (input <- io.Source.stdin.getLines) {
      val sparql = input match {
        case "exit" => return
        case _ => readSPARQL
      }

      try {
        val result = sparqlQuery(sparql)
        println("\nSPARQL: " + sparql + "\nResult: " + result)
      } catch {
        case e: Exception => println(e)
      }

      print(prompt)
    }
  }

  def handlerUpdate(prompt: String): Unit = {
    println(sparqlConsoleHelp)
    print(prompt)

    for (input <- io.Source.stdin.getLines) {
      val sparql = input match {
        case "exit" => return
        case _ => readSPARQL
      }

      try {
        sparqlUpdate(sparql)
        println("\nSPARQL: " + sparql + "\nExecuted normally")
      } catch {
        case e: Exception => println(e)
      }

      print(prompt)
    }
  }

  def handlerRest(prompt: String): Unit = {
    print(prompt)

    for (input <- io.Source.stdin.getLines) {
      val output = input.split(" ").toList match {
        case "exit" :: Nil => return
        case "head" :: obj :: Nil => "HEAD object [%s]".format(obj)
        case "get" :: obj :: Nil => "GET object [%s]".format(obj)
        case "put" :: obj :: Nil => "PUT object [%s]".format(obj)
        case "post" :: obj :: Nil => "POST object [%s]".format(obj)
        case "delete" :: obj :: Nil => "DELETE object [%s]".format(obj)
        case "" :: Nil => ""
        case _ => "Unknown REST command: " + input + "\n" +
          "Available commands:\n" +
          "[head] object: \t briefing of object\n" +
          "[get] object: \t get object, list collection content\n" +
          "[put] object: \t replace or create object or collection\n" +
          "[post] object: \t create new entry in collection\n" +
          "[delete] object: \t delete collection or object\n" +
          "NOTE:\n" +
          "just demo, no wildcard/additional parameter support.\n" +
          "use [exit] to go back"
      }
      println(output)

      print(prompt)
    }
  }

  def handlerUnknown(prompt: String) = println("No valid command handler is associated\n" +
    "Available handlers: " +
    handlerMap.flatMap { case (k, v) => List(k) }.mkString("[", "] [", "]"))

  def enterDSCLI(mode: String) =
    handlerMap.getOrElse(mode, (handlerUnknown _, null)) match { case (h, s) => h(mode + " > ") }

  val help = handlerMap.flatMap {
    case (m, (h, s)) => List("  %s: \t %s".format(m, s))
  }.mkString("\n")

}
