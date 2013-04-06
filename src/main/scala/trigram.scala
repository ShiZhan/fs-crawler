/**
 * @author ShiZhan
 * 2013
 * TriGraM main program
 * provides 3 entries:
 * 1. show help
 * 2. show version
 * 3. get data from specified source and put into local storage
 *    allowed source: directory tree, RDF/OWL model file
 * default entry:
 *    enter console
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

  def main(args: Array[String]) = {
    println("Triple Graph based Metadata storage - TriGraM")

    if (args.length == 0) Console.run
    else {
      type OptionMap = Map[Symbol, Any]

      def nextOption(map: OptionMap, list: List[String]): OptionMap = {
        list match {
          case Nil => map
          case "-h" :: tail => nextOption(map ++ Map('help -> true), tail)
          case "--help" :: tail => nextOption(map ++ Map('help -> true), tail)
          case "-v" :: tail => nextOption(map ++ Map('version -> true), tail)
          case "--version" :: tail => nextOption(map ++ Map('version -> true), tail)
          case "-i" :: model :: tail =>
            nextOption(map ++ Map('resource -> model), tail)
          case "--import" :: model :: tail =>
            nextOption(map ++ Map('resource -> model), tail)
          case option :: tail =>
            println("Incorrect option: " + option)
            sys.exit(1)
        }
      }
      val options = nextOption(Map(), args.toList)

      if (options.contains('help)) println(usage)
      else if (options.contains('version)) println(getVersion)
      else if (options.contains('resource)) Importer.load(options('resource).toString)
    }
  }

}

object TrigramTranslator {

  val defaultInType = "directory"
  val defaultSource = "./"
  val defaultTarget = "model.rdf"

  val usage = """
usage: TrigramTranslator [-h] [-v] [-l] [-t] TYPE [-i] INPUT [-o] OUTPUT
 -h,--help                print this message
 -v,--version             show program version
 -l,--list                list all translatable resources
 -t,--type TYPE           type of input resource
 -i,--input SOURCE        input resource
 -o,--output TARGET       output target

default:
 TYPE   - [%s]
 SOURCE - [%s]
 TARGET - [%s]
""".format(defaultInType, defaultSource, defaultTarget)

  def main(args: Array[String]) = {
    println("TriGraM metadata translator")

    type OptionMap = Map[Symbol, Any]

    def nextOption(map: OptionMap, list: List[String]): OptionMap = {
      list match {
        case Nil => map
        case "-h" :: tail => nextOption(map ++ Map('help -> true), tail)
        case "--help" :: tail => nextOption(map ++ Map('help -> true), tail)
        case "-v" :: tail => nextOption(map ++ Map('version -> true), tail)
        case "--version" :: tail => nextOption(map ++ Map('version -> true), tail)
        case "-l" :: tail => nextOption(map ++ Map('list -> true), tail)
        case "--list" :: tail => nextOption(map ++ Map('list -> true), tail)
        case "-t" :: inType :: tail =>
          nextOption(map ++ Map('intype -> inType), tail)
        case "--type" :: inType :: tail =>
          nextOption(map ++ Map('intype -> inType), tail)
        case "-i" :: source :: tail =>
          nextOption(map ++ Map('source -> source), tail)
        case "--input" :: source :: tail =>
          nextOption(map ++ Map('source -> source), tail)
        case "-o" :: target :: tail =>
          nextOption(map ++ Map('target -> target), tail)
        case "--output" :: target :: tail =>
          nextOption(map ++ Map('target -> target), tail)
        case option :: tail =>
          println("Incorrect option: " + option)
          sys.exit(1)
      }
    }
    val options = nextOption(Map(), args.toList)

    if (options.contains('help)) println(usage)
    else if (options.contains('version)) println(getVersion)
    else if (options.contains('list)) println("translatable resources: ")
    else {
      val t = if (options.contains('intype)) options('intype).toString else defaultInType
      val i = if (options.contains('source)) options('source).toString else defaultSource
      val o = if (options.contains('target)) options('target).toString else defaultTarget

      println("translating [%s] as [%s] to [%s]".format(i, t, o))
    }
  }

}
