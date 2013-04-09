/**
 * @author ShiZhan
 * 2013
 * TriGraM main program
 * provides 3 entries:
 * 1. show help
 * 2. show version
 * 3. get data from specified model and put into local storage
 * default entry:
 *    enter console
 *
 * TriGraM translator program
 * provides 3 entries:
 * 1. show help
 * 2. show version
 * 3. translate specified source to TriGraM model
 * default entry:
 *    translate current directory tree structure to model
 */
import core.{ Console, Importer }
import util.Version.getVersion

object Trigram {

  val usage = """
usage: Trigram [-h] [-v] [-i]
 -h,--help                print this message
 -v,--version             show program version
 -i,--import MODEL        import model

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
      case "-i" :: m :: tail => nextOption(map ++ Map('resource -> m), tail)
      case "--import" :: m :: tail => nextOption(map ++ Map('resource -> m), tail)
      case option :: tail => println("Incorrect option: " + option); sys.exit(1)
    }
  }

  def main(args: Array[String]) = {
    println("Triple Graph based Metadata storage - TriGraM")

    if (args.length == 0) Console.run
    else {
      val options = nextOption(Map(), args.toList)

      if (options.contains('help)) println(usage)
      else if (options.contains('version)) println(getVersion)
      else if (options.contains('resource)) Importer.load(options('resource).toString)
    }
  }

}

import core.Translator

object TrigramTranslator {

  val defaultInType = "directory"
  val defaultSource = "./"
  val defaultTarget = "model.rdf"

  val usage = """
usage: TrigramTranslator [-h] [-v] [-l] [-t] TYPE [-i] INPUT [-o] OUTPUT
 -h,--help                print this message
 -v,--version             show program version
 -t,--type TYPE           type of input resource [default: %s]
 -i,--input SOURCE        input resource         [default: %s]
 -o,--output TARGET       output target          [default: %s]
""".format(defaultInType, defaultSource, defaultTarget) +
    "\ntranslatable resources:\n" + Translator.help

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

    if (options.contains('help)) println(usage)
    else if (options.contains('version)) println(getVersion)
    else {
      val t = options.getOrElse('intype, defaultInType).toString
      val i = options.getOrElse('source, defaultSource).toString
      val o = options.getOrElse('target, defaultTarget).toString

      println("translating [%s] as [%s] to [%s]".format(i, t, o))

      Translator.run(t, i, o)
    }
  }

}