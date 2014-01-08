/**
 * command handler
 */
package console

/**
 * @author ShiZhan
 * command handlers for reading and executing SPARQL in triple store
 */
class Handler(store: Store) {
  def doQuery(sparql: String) = {
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

  def doUpdate(sparql: String) = {
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