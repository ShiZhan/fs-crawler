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

  import jena.rdfcat
  import modeler.{ Modelers, CimVocabulary, Merger }
  import util.Version

  val usage = s"""
usage: Translator [flags] <arguments>
 -h,--help      Print this message
 -v,--version   Show program version

 build-in utilities:
 -c,--combine <MODEL...>         Combine multiple models
 -C,--rdfcat <MODEL...>          Jena rdfcat utility warpper
                                 Show contents of RDF/OWL file(s)
 -V,--vocabulary CIM_Schema_XML  Update CIM vocabulary for use in modelers
                                 [CIM Schema XML] can be downloaded from DMTF:
                                 http://dmtf.org/standards/cim
 -g,--gather MODEL               Gather CIM imports into given model
                                 Based on the <OWL.imports> in [MODEL].
 -m,--modeler MODELER <args...>  Use [modeler] with arguments (below)
                                 Each [modeler] has its own arguments.

 supported modelers:
""" + Modelers.help

  val notEnoughArgs = "Not enough parameters, see help."
  val incorrectArgs = "Incorrect parameters, see help."

  def main(args: Array[String]) = {
    println("TriGraM metadata translator")

    if (args.length == 0) println(usage)
    else if (args(0) == "-h" | args(0) == "--help") println(usage)
    else if (args(0) == "-v" | args(0) == "--version") println(Version.get)
    else if (args(0) == "-c" | args(0) == "--combine") println("Combine models")
    else if (args(0) == "-C" | args(0) == "--rdfcat") println("Jena rdfcat")
    else if (args(0) == "-V" | args(0) == "--vocabulary")
      if (args.length > 1) {
        CimVocabulary.generator(args(1))
        println("CIM Schema Vocabulary files in [%s] are updated."
          .format(util.Config.CIMDATA))
      } else println(notEnoughArgs)
    else if (args(0) == "-g" | args(0) == "--gather")
      if (args.length > 1) {
        Merger.merge(args(1))
        println("All imported CIM classes of [%s] gathered.".format(args(1)))
      } else println(notEnoughArgs)
    else if (args(0) == "-m" | args(0) == "--modeler") {
      if (args.length > 2) {
        val m = args(1)
        val o = args.drop(2)

        println("invoking [%s] modeler with options [%s]".format(m, o.mkString(" ")))

        Modelers.run(m, o)
      } else println(notEnoughArgs)
    } else println(incorrectArgs)
  }

}