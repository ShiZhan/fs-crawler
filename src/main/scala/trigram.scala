/**
 * @author ShiZhan
 * 2013
 * TriGraM main program
 * provides 5 entries:
 * 1. show help
 * 2. show version
 * 3. get data from specified source and put into local storage
 *    allowed source: directory tree, typed file
 * 4. start server
 * 5. enter console
 */
import core.{ Server, Console, ModelFactory }
import util.Version.getVersion

object trigram {

  val usage = """
usage: trigram [-h] [-v] [-i] [-s] [-c]
 -h,--help                    print this message
 -v,--version                 show program version
 -i,--import <ITEM_ROOT>      import metadata locally from given item
 -s,--server <PORT>           start server on specified port
 -c,--console <IP:PORT>       open console on specified address
"""

  def main(args: Array[String]) {
    println("Triple Graph based Metadata storage - TriGraM")

    if (args.length == 0) println(usage)

    val arglist = args.toList
    type OptionMap = Map[Symbol, Any]

    def nextOption(map : OptionMap, list: List[String]) : OptionMap = {
      list match {
        case Nil => map
        case "-h" :: tail =>     nextOption(map ++ Map('help -> true), tail)
        case "--help" :: tail => nextOption(map ++ Map('help -> true), tail)
        case "-v" :: tail =>        nextOption(map ++ Map('version -> true), tail)
        case "--version" :: tail => nextOption(map ++ Map('version -> true), tail)
        case "-i" :: root :: tail =>
            nextOption(map ++ Map('resource -> root), tail)
        case "--import" :: root :: tail =>
            nextOption(map ++ Map('resource -> root), tail)
        case "-s" :: value :: tail =>
            nextOption(map ++ Map('port -> value), tail)
        case "--server" :: value :: tail =>
            nextOption(map ++ Map('port -> value), tail)
        case "-c" :: address :: tail =>
            nextOption(map ++ Map('remote -> address), tail)
        case "--console" :: address :: tail =>
            nextOption(map ++ Map('remote -> address), tail)
        case option :: tail => println("Incorrect option: "+option) 
                               sys.exit(1)
      }
    }
    val options = nextOption(Map(), arglist)

    if (options.contains('help)) println(usage)
    else if (options.contains('version)) println(getVersion)
    else if (options.contains('resource)) ModelFactory.load(options('resource).toString)
    else if (options.contains('port)) Server.run(options('port).toString)
    else if (options.contains('remote)) Console.run(options('remote).toString.split(":"))
  }

}
