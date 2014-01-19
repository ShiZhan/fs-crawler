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
  import helper.{ GetString, Platform, Version }
  import common.ModelEx._
  import Infer._

  private val TGMVER = Version.get
  val status = s"""
TriGraM:     $TGMVER
  code:      $TGMROOT
  data:      $TGMDATA
  CIM:       $CIMDATA""" + Platform.BRIEFING

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

  def infer(rdfs: String, data: String) =
    try { riotcmd.infer.main("--rdfs=" + rdfs, data) }
    catch { case e: Exception => println(e) }

  private val store = new Store(TGMDATA)

  def shutdown = store.close

  def doQuery(qArgs: List[String]) = {
    val sparql = if (qArgs == Nil) GetString.fromConsole else GetString.fromFile(qArgs.head)
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

  def doUpdate(uArgs: List[String]) = {
    val sparql = if (uArgs == Nil) GetString.fromConsole else GetString.fromFile(uArgs.head)
    try {
      val t1 = compat.Platform.currentTime
      store.update(sparql)
      val t2 = compat.Platform.currentTime
      println("Update Executed in %d milliseconds".format(t2 - t1))
    } catch {
      case e: Exception => e.printStackTrace
    }
  }

  def inferWithSchema(modelFN: String, schemaFNs: List[String]) = {
    val data = load(modelFN)
    val output = modelFN + "-infered.owl"
    if (Nil == schemaFNs)
      data.infer.validateAndSave(output)
    else {
      val schema = load(schemaFNs.head) // chaining ...
      data.inferWithOWL(schema).validateAndSave(output)
    }
  }

  def inferWithRule(modelFN: String, ruleFNs: List[String]) = {
    val data = load(modelFN)
    val output = modelFN + "-infered.owl"
    if (Nil == ruleFNs) {
      data.inferWithRule(defaultRules).validateAndSave(output)
    } else {
      val rules = ruleFNs.map(GetString.fromFile).map(parseRules)
      for (r <- rules) // chaining ...
        data.inferWithRule(r).validateAndSave(output)
    }
  }
}