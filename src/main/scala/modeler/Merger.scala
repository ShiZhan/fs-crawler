/**
 * Model Merger
 */
package modeler

/**
 * @author ShiZhan
 * 1. CIM Model Merger
 * 2. Generic model combiner
 */
object Merger {
  import scala.collection.JavaConversions._
  import cim.{ Vocabulary => CIM }
  import common.ModelEx._

  private def readCimImports(modelFile: String): List[String] = {
    val m = load(modelFile)
    val importFiles = m.getImports
      .map { _.getObject.toString }
      .filter(CIM.isCimURI)
      .map(CIM.PURL2FN).toList
    m.close
    if (importFiles.isEmpty) List()
    else importFiles ::: importFiles.flatMap(readCimImports)
  }

  def gather(output: String, selected: List[String]) = {
    val fns = { if (Nil == selected) Modelers.tbox.toList else selected }.map(CIM.FFN)
    val all = { fns.flatMap(readCimImports) ++ fns }.distinct
    println("[%d] CIM classes to gather:".format(all.length))
    all foreach println
    all.asModels.join.rebase.store(output)
  }

  def combine(all: List[String]) = all.asModels.combine.store(all.head + "-sum.owl")
}