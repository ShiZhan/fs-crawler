/**
 * @author ShiZhan
 * 2013
 * TriGraM main program
 * provides 3 entries:
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
 */
object Trigram {

  import tdb.tdbloader.{ main => loader }
  import tdb.tdbquery.{ main => query }
  import tdb.tdbupdate.{ main => update }
  import core.Console
  import core.Store.defaultLocation
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
      case "-h" :: tail => nextOption(map ++ Map('help -> true), tail)
      case "--help" :: tail => nextOption(map ++ Map('help -> true), tail)
      case "-v" :: tail => nextOption(map ++ Map('version -> true), tail)
      case "--version" :: tail => nextOption(map ++ Map('version -> true), tail)
      case "-i" :: m :: tail => nextOption(map ++ Map('model -> m), tail)
      case "--import" :: m :: tail => nextOption(map ++ Map('model -> m), tail)
      case "-q" :: q :: tail => nextOption(map ++ Map('query -> q), tail)
      case "--query" :: q :: tail => nextOption(map ++ Map('query -> q), tail)
      case "-u" :: u :: tail => nextOption(map ++ Map('update -> u), tail)
      case "--update" :: u :: tail => nextOption(map ++ Map('update -> u), tail)
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
        val modelFile = options('model).toString
        loader("--loc=" + defaultLocation, modelFile)
      } else if (options.contains('query)) {
        val queryFile = options('query).toString
        query("--loc=" + defaultLocation, "--query=" + queryFile)
      } else if (options.contains('update)) {
        val updateFile = options('update).toString
        update("--loc=" + defaultLocation, "--update=" + updateFile)
      }
    }
  }

}

object TrigramTranslator {

  import modeler.Modelers
  import util.Version

  val defaultInType = modeler.Directory.key
  val defaultSource = "."
  val defaultTarget = "model.rdf"

  val usage = s"""
usage: TrigramTranslator [-h] [-v] [-m] [-t] TYPE [-i] INPUT [-o] OUTPUT
 -h,--help                Print this message
 -v,--version             Show program version
 -m,--meta                1. If this flag is set, the translator will generate
                          meta-model (TBox) for given type [-t].
                          2. If not, the translator will translate specified
                          source [-i] as type [-t] to target model [-o] (ABox)
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
      case "-m" :: tail => nextOption(map ++ Map('meta -> true), tail)
      case "--meta" :: tail => nextOption(map ++ Map('meta -> true), tail)
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
    else if (options.contains('meta)) {
      val t = options.getOrElse('intype, defaultInType).toString

      println("generating TBox [%s]".format(t))

      Modelers.getTBox(t)
    } else {
      val t = options.getOrElse('intype, defaultInType).toString
      val i = options.getOrElse('source, defaultSource).toString
      val o = options.getOrElse('target, defaultTarget).toString

      println("translating [%s] as [%s] to ABox [%s]".format(i, t, o))

      Modelers.getABox(t, i, o)
    }
  }

}

object TrigramThinker {

  import core.Thinker

  val usage = """
usage: Thinker [TBox] [ABox] [output]
"""

  def main(args: Array[String]) =
    if (args.length < 3) println(usage)
    else Thinker.inferAndSave(args(0), args(1), args(2))

}