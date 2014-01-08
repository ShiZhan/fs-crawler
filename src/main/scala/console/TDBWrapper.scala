/**
 * Jena TDB Wrapper
 */
package console

/**
 * @author ShiZhan
 * Jena TDB Wrapper
 * Use Jena TDB native program directly in Trigram.
 * All programs are provided by Jena command line interfaces
 * through generic console scripts.
 * Wrap these programs into "try ... catch" for encapsulation.
 */
object TDBWrapper {
  import util.Config.TGMDATA

  def loader(modelFile: String) =
    try { tdb.tdbloader.main("--loc=" + TGMDATA, modelFile) }
    catch { case e: Exception => e.printStackTrace }

  def info =
    try { tdb.tdbstats.main("--loc=" + TGMDATA) }
    catch { case e: Exception => e.printStackTrace }

  def query(queryFile: String) =
    try { tdb.tdbquery.main("--loc=" + TGMDATA, "--query" + queryFile) }
    catch { case e: Exception => e.printStackTrace }

  def update(updateFile: String) =
    try { tdb.tdbupdate.main("--loc=" + TGMDATA, "--update" + updateFile) }
    catch { case e: Exception => e.printStackTrace }
}