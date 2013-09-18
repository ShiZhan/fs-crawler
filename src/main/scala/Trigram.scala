/**
 * @author ShiZhan
 * 2013
 * TriGraM main program
 * provides 5 entries:
 * 1. show help
 * 2. show version
 * 3. import specified model into local storage
 * 4. query local storage
 * 5. update local storage
 * default entry:
 *    enter console
 *
 * TriGraM translator program
 * provides 3 entries:
 * 1. show help
 * 2. show version
 * 3. translate specified source to TriGraM model
 *
 * CIM Schema Vocabulary generate/update program
 * provides 1 entries:
 * 1. read CIM Schema (XML) and write Vocabulary files into TGMROOT
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

  val defaultInType = modeler.Directory.key
  val defaultSource = "."
  val defaultTarget = "model.owl"

  val usage = s"""
usage: TrigramTranslator [-h] [-v] [-m] [-t] TYPE [-i] INPUT [-o] OUTPUT
 -h,--help                Print this message
 -v,--version             Show program version
 -t,--type TYPE           Type of input resource [default: $defaultInType]
 -i,--input SOURCE        Input resource         [default: $defaultSource]
 -o,--output TARGET       Output target          [default: $defaultTarget]

 supported types:
""" + Modelers.getHelp

  type OptionMap = Map[Symbol, Any]

  def nextOption(map: OptionMap, list: List[String]): OptionMap = {
    list match {
      case Nil => map
      case "-h" :: tail => nextOption(map ++ Map('help -> true), tail)
      case "--help" :: tail => nextOption(map ++ Map('help -> true), tail)
      case "-v" :: tail => nextOption(map ++ Map('version -> true), tail)
      case "--version" :: tail => nextOption(map ++ Map('version -> true), tail)
      case "-t" :: t :: tail => nextOption(map ++ Map('intype -> t), tail)
      case "--type" :: t :: tail => nextOption(map ++ Map('intype -> t), tail)
      case "-i" :: i :: tail => nextOption(map ++ Map('source -> i), tail)
      case "--input" :: i :: tail => nextOption(map ++ Map('source -> i), tail)
      case "-o" :: o :: tail => nextOption(map ++ Map('target -> o), tail)
      case "--output" :: o :: tail => nextOption(map ++ Map('target -> o), tail)
      case option :: tail => println("Incorrect option: " + option); sys.exit(1)
    }
  }

  def main(args: Array[String]) = {
    println("TriGraM metadata translator")

    val options = nextOption(Map(), args.toList)

    if (args.length == 0 | options.contains('help)) println(usage)
    else if (options.contains('version)) println(Version.get)
    else {
      val t = options.getOrElse('intype, defaultInType).toString
      val i = options.getOrElse('source, defaultSource).toString
      val o = options.getOrElse('target, defaultTarget).toString

      println("translating [%s] as [%s] to model [%s]".format(i, t, o))

      Modelers.run(t, i, o)
    }
  }

}

object CimVocabGen {

  import modeler.CimVocabulary.generator
  import util.Config.TGMROOT

  def main(args: Array[String]) = {
    println("CIM Schema Vocabulary generator")

    if (args.length < 1)
      println("run with <CIM Schema XML>, which can be downloaded from DMTF.")
    else {
      generator(args(0))
      println(s"CIM Schema Vocabulary files are saved in [$TGMROOT].")
    }
  }

}

object CimModelMerger {
  private val usage = """
  This program is for use with CimSchemaEx modeler, which generates
  separate, dependent sub-models for use by dedicated modelers.
  The purpose is to merge required sub-models into one aggregated model.
  The merged model can than be easily inferred, imported or transferred.
  The merge is decided by sub-model dependency implied by owl:import.
  After the merge process, all the owl:import will be cleaned.
  To compare with models based on complete CIM model: CIM_All.owl.
  The output model could be significantly smaller.
  """

  def main(args: Array[String]) = {
    println("CIM Schema based model merger")

    if (args.length < 1)
      println(usage)
    else {
      println("model merged.")
    }
  }
}
