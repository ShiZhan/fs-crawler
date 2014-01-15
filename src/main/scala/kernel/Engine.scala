/**
 * TriGraM engine
 */
package kernel

/**
 * @author ShiZhan
 * TriGraM engine
 */
object Engine {
  import helper.Config.{ TGMROOT, TGMDATA, CIMDATA }
  import helper.{ Platform, Version }

  private val TGMVER = Version.get
  val status = s"""
TriGraM:     $TGMVER
  code:      $TGMROOT
  data:      $TGMDATA
  CIM:       $CIMDATA""" + Platform.BRIEFING

  // Jena TDB CLI
  def tdbloader(modelFile: String) =
    try { tdb.tdbloader.main("--loc=" + TGMDATA, modelFile) }
    catch { case e: Exception => println(e) }

  def tdbinfo =
    try { tdb.tdbstats.main("--loc=" + TGMDATA) }
    catch { case e: Exception => println(e) }

  def tdbquery(queryFile: String) =
    try { tdb.tdbquery.main("--loc=" + TGMDATA, "--query=" + queryFile) }
    catch { case e: Exception => println(e) }

  def tdbupdate(updateFile: String) =
    try { tdb.tdbupdate.main("--loc=" + TGMDATA, "--update=" + updateFile) }
    catch { case e: Exception => println(e) }

  // Jena TDB API
  private val store = new TDBAPI(TGMDATA)

  def shutdown = store.close

  // additional handlers for executing SPARQL in triple store
  def doQuery(sparql: String) = {
    try {
      val t1 = compat.Platform.currentTime
      val result = store.queryAny(sparql)
      val t2 = compat.Platform.currentTime
      println(result)
      println("Query executed in %d milliseconds".format(t2 - t1))
    } catch {
      case e: Exception => e.printStackTrace
    }
  }

  def doUpdate(sparql: String) = {
    try {
      val t1 = compat.Platform.currentTime
      store.update(sparql)
      val t2 = compat.Platform.currentTime
      println("Update Executed in %d milliseconds".format(t2 - t1))
    } catch {
      case e: Exception => e.printStackTrace
    }
  }
}