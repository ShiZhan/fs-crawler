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
      val t1 = compat.Platform.currentTime
      val result = store.queryAny(sparql)
      val t2 = compat.Platform.currentTime
      println(result)
      "Query executed in %d milliseconds".format(t2 - t1)
    } catch {
      case e: Exception => "Exception:\n" + e.toString
    }
  }

  def doUpdate = {
    val sparql = readSPARQL

    try {
      val t1 = compat.Platform.currentTime
      store.update(sparql)
      val t2 = compat.Platform.currentTime
      "Update Executed in %d milliseconds".format(t2 - t1)
    } catch {
      case e: Exception => "Exception:\n" + e.toString
    }
  }

}