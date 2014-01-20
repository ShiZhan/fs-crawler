/**
 * Model Merger
 */
package kernel

/**
 * @author ShiZhan
 * 1. CIM Model Merger
 * 2. Generic model combiner
 */
object Merger {
  import scala.collection.JavaConversions.asScalaIterator
  import cim.Vocabulary.{ FFN, PURL2FN, isCimURI }
  import common.ModelEx._
  import modeler.Modelers

  private def readCimImports(modelFile: String): List[String] = {
    val m = load(modelFile)
    val importFiles = m.getImports
      .map { _.getObject.toString }.filter(isCimURI).map(PURL2FN).toList
    m.close
    if (importFiles.isEmpty) List()
    else importFiles ::: importFiles.flatMap(readCimImports)
  }

  def gather(output: String, selected: List[String]) = {
    val files = { if (Nil == selected) Modelers.tbox.toList else selected }.map(FFN)
    val all = { files.flatMap(readCimImports) ++ files }.distinct
    println("[%d] CIM classes to gather:".format(all.length))
    all.foreach(println)
    all.asModels.join.rebase.store(output)
  }

  def combine(all: List[String]) = all.asModels.combine.store(all.head + "-sum.owl")
}