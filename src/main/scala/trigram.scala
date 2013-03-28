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

object trigram {

  val usage = """
usage: trigram [-h] [-v] [-i]
 -h,--help                    print this message
 -v,--version                 show program version
 -i,--import <ITEM_ROOT>      import metadata from given item

 no argument                  enter console
"""

  def main(args: Array[String]) = {
    println("Triple Graph based Metadata storage - TriGraM")

    if (args.length == 0) Console.run

    type OptionMap = Map[Symbol, Any]

    def nextOption(map: OptionMap, list: List[String]): OptionMap = {
      list match {
        case Nil => map
        case "-h" :: tail => nextOption(map ++ Map('help -> true), tail)
        case "--help" :: tail => nextOption(map ++ Map('help -> true), tail)
        case "-v" :: tail => nextOption(map ++ Map('version -> true), tail)
        case "--version" :: tail => nextOption(map ++ Map('version -> true), tail)
        case "-i" :: root :: tail =>
          nextOption(map ++ Map('resource -> root), tail)
        case "--import" :: root :: tail =>
          nextOption(map ++ Map('resource -> root), tail)
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