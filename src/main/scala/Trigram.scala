/**
 * @author ShiZhan
 * @year 2013
 * @name TriGraM Project
 */
object Trigram {

  import tdb.tdbloader.{ main => loader }
  import tdb.tdbquery.{ main => query }
  import tdb.tdbupdate.{ main => update }
  import console.Console
  import util.Config.TGMDATA
  import util.Version

  val usage = """
usage: Trigram [-h] [-v] [-i] [-q] [-u]
 -h,--help                print this message
 -v,--version             show program version
 -i,--import MODEL        import model
 -q,--query SPARQL        http://www.w3.org/TR/sparql11-query/
 -u,--update SPARQL       http://www.w3.org/TR/sparql11-update/

 no argument              enter console
"""

  type OptionMap = Map[Symbol, Any]

  def nextOption(map: OptionMap, list: List[String]): OptionMap = {
    list match {
      case Nil => map
      case "-h" :: tail => map ++ Map('help -> true)
      case "--help" :: tail => map ++ Map('help -> true)
      case "-v" :: tail => map ++ Map('version -> true)
      case "--version" :: tail => map ++ Map('version -> true)
      case "-i" :: modelList => map ++ Map('model -> modelList)
      case "--import" :: modelList => map ++ Map('model -> modelList)
      case "-q" :: q :: tail => map ++ Map('query -> q)
      case "--query" :: q :: tail => map ++ Map('query -> q)
      case "-u" :: u :: tail => map ++ Map('update -> u)
      case "--update" :: u :: tail => map ++ Map('update -> u)
      case option :: tail => println("Incorrect option: " + option); sys.exit(1)
    }
  }

  def main(args: Array[String]) = {
    println("Triple Graph based Metadata storage - TriGraM")

    if (args.length == 0) Console.run
    else {
      val options = nextOption(Map(), args.toList)

      if (options.contains('help)) println(usage)
      else if (options.contains('version)) println(Version.get)
      else if (options.contains('model)) {
        val modelList = options('model).asInstanceOf[List[String]]
        modelList.foreach(loader(s"--loc=$TGMDATA", _))
      } else if (options.contains('query)) {
        val queryFile = options('query).toString
        query(s"--loc=$TGMDATA", "--query=" + queryFile)
      } else if (options.contains('update)) {
        val updateFile = options('update).toString
        update(s"--loc=$TGMDATA", "--update=" + updateFile)
      }
    }
  }

}

object TrigramTranslator {

  import modeler.Modelers
  import util.Version

  val usage = s"""
usage: TrigramTranslator [-h|-v|-m MODELER <arguments>]
 -h,--help                Print this message
 -v,--version             Show program version
 -m,--modeler MODELER     Use <modeler>

 supported modelers:
""" + Modelers.help

  def main(args: Array[String]) = {
    println("TriGraM metadata translator")

    if (args.length == 0) println(usage)
    else if (args(0) == "-h" | args(0) == "--help") println(usage)
    else if (args(0) == "-v" | args(0) == "--version") println(Version.get)
    else if (args(0) == "-m" | args(0) == "--modeler") {
      if (args.length > 2) {
        val m = args(1)
        val o = args.drop(2)

        println("invoking [%s] modeler with options [%s]".format(m, o.mkString(" ")))

        Modelers.run(m, o)
      } else println("insufficient parameters, see help.")
    } else println("invalid option [%s], see help.".format(args(0)))
  }

}

object CimVocabGen {

  import modeler.CimVocabulary.generator
  import util.Config.CIMDATA

  private val usage = """
  run with <CIM Schema XML>, which can be downloaded from DMTF.
  """

  def main(args: Array[String]) = {
    println("CIM Schema Vocabulary generator")

    if (args.length < 1)
      println(usage)
    else {
      generator(args(0))
      println(s"CIM Schema Vocabulary files are saved in [$CIMDATA].")
    }
  }

}

object CimModelMerger {

  import modeler.Merger.merge

  private val usage = """
  run with <model to merge>
  """

  def main(args: Array[String]) = {
    println("CIM Schema based model merger")

    if (args.length < 1)
      println(usage)
    else {
      merge(args(0))
      println("model merged.")
    }
  }

}