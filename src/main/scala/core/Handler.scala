/**
 * command handler
 */
package core

/**
 * @author ShiZhan
 * implements Domain Specific Command handlers
 * translates DSL commands into triple store operations
 * for use in Console application
 */
class Handler(store: Store) {

  type Handler = String => Unit
  type HandlerMap = Map[String, (Handler, String)]
  private val hMap: HandlerMap = Map(
    "query" -> (handlerQuery, "SPARQL query interpreter"),
    "update" -> (handlerUpdate, "SPARQL update interpreter"))
  private val hMapDefault = (handlerUnknown _, null)

  private def readSPARQL = {
    println("input SPARQL below, end with Ctrl+E.")
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
        val result = store.sparqlQuery(sparql)
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
        store.sparqlUpdate(sparql)
        println("\nSPARQL: " + sparql + "\nExecuted normally")
      } catch {
        case e: Exception => println(e)
      }

      print(prompt)
    }
  }

  def handlerUnknown(prompt: String) =
    println("No valid command handler is associated\n" +
      "Available handlers: " +
      hMap.map { case (k, v) => List(k) }.mkString("[", "] [", "]"))

  def enterDSCLI(mode: String) =
    hMap.getOrElse(mode, hMapDefault) match { case (h, s) => h(mode + " > ") }

  val help = hMap.map {
    case (m, (h, s)) => "  %s: \t %s".format(m, s)
  }.mkString("\n")

}

object Handler {
  def apply(store: Store) = new Handler(store)
}