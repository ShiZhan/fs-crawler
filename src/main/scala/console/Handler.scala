/**
 * command handler
 */
package console

/**
 * @author ShiZhan
 * command handlers for reading and executing SPARQL in triple store
 */
class Handler(store: Store) {

  private def readSPARQL = {
    println("input SPARQL below, end with Ctrl+E.")
    io.Source.fromInputStream(System.in).takeWhile(_ != 5.toChar).mkString
  }

  def doQuery: Unit = {
    val sparql = readSPARQL

    try {
      val result = store.queryAny(sparql)
      println("\nResult: " + result)
    } catch {
      case e: Exception => println(e)
    }
  }

  def doUpdate: Unit = {
    val sparql = readSPARQL

    try {
      store.update(sparql)
      println("\nSPARQL: " + sparql + "\nExecuted normally")
    } catch {
      case e: Exception => println(e)
    }
  }

}