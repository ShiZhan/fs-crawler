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

  def doQuery = {
    val sparql = readSPARQL

    try {
      val result = store.queryAny(sparql)
      println(result)
      "Query executed normally"
    } catch {
      case e: Exception => "Exception:\n" + e.toString
    }
  }

  def doUpdate = {
    val sparql = readSPARQL

    try {
      store.update(sparql)
      "Update Executed normally"
    } catch {
      case e: Exception => "Exception:\n" + e.toString
    }
  }

}