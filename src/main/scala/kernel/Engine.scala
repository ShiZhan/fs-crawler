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

  def doInferWithOWL(dataFN: String, owlFN: String, output: String) = {
    val data = load(dataFN)
    val schema = load(owlFN)
    val deductions = data.infer(schema)
    deductions.validateAndSave(output)
  }

  def doInferWithRule(dataFN: String, ruleFN: String, output: String) = {
    val data = load(dataFN)
    val rule = GetString.fromFile(ruleFN)
    val deductions = data.infer(rule)
    deductions.validateAndSave(output)
  }
}