/**
 * TriGraM engine
 */
package kernel

/**
 * @author ShiZhan
 * TriGraM engine
 */
object Engine {
  import helper.Config.{ TGMROOT, TGMDATA }
  import helper.{ GetString, Platform, Version }
  import common.ModelEx._
  import Infer._

  private val TGMVER = Version.get
  val status = s"""
TriGraM:     $TGMVER
  code:      $TGMROOT
  data:      $TGMDATA""" + Platform.BRIEFING

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

  def timedQuery(sparql: String) = if ("" != sparql) {
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
  def doQueryFromConsole = timedQuery(GetString.fromConsole)
  def doQueryFromFile(fileName: String) = timedQuery(GetString.fromFile(fileName))
  def doQuery(qArgs: List[String]) =
    if (Nil == qArgs) doQueryFromConsole else qArgs.foreach(doQueryFromFile)

  def timedUpdate(sparql: String) = {
    try {
      val t1 = compat.Platform.currentTime
      store.update(sparql)
      val t2 = compat.Platform.currentTime
      println("Update Executed in %d milliseconds".format(t2 - t1))
    } catch {
      case e: Exception => e.printStackTrace
    }
  }
  def doUpdateFromConsole = timedUpdate(GetString.fromConsole)
  def doUpdateFromFile(fileName: String) = timedUpdate(GetString.fromFile(fileName))
  def doUpdate(uArgs: List[String]) =
    if (Nil == uArgs) doUpdateFromConsole else uArgs.foreach(doUpdateFromFile)

  def infer(modelFN: String, ruleFNs: List[String]) = {
    val data = load(modelFN)
    def output(suffix: String) = s"$modelFN-$suffix.owl"
    if (Nil == ruleFNs) {
      data.infer(defaultRules).validateAndSave(output("deduction"))
    } else {
      (data /: ruleFNs) { (baseModel, ruleFN) =>
        val ruleString = GetString.fromFile(ruleFN)
        val rules = parseRules(ruleString)
        val t1 = compat.Platform.currentTime
        val result = baseModel.infer(rules)
        val t2 = compat.Platform.currentTime
        println("Inferring Executed in %d milliseconds".format(t2 - t1))
        result.validateAndSave(output(new java.io.File(ruleFN).getName))
        baseModel union result.getDeductionsModel
      } store (output("final"))
    }
  }

  def combine(files: List[String]) =
    files.asModels.join.store(files.head + "-combined.n3", "N3")
}