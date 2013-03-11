/**
 * @author ShiZhan
 * 2013
 * TriGraM main program
 * provides 5 entries:
 * 1. show help
 * 2. show version
 * 3. get data from specified source {directory tree, typed file}
 * 4. start server
 * 5. enter console
 */
import org.apache.commons.cli._
import core.{ Server, Console, ModelFactory }
import util.Version.getVersion

object trigram {

  def main(args: Array[String]) =
    if (args.length == 0) println("Use '-h' option to access help")
    else {

      println("Triple Graph based Metadata storage - TriGraM")

      val parser = new PosixParser()

      val options = new Options()
        .addOption("h", "help", false, "print this message")
        .addOption("v", "version", false, "show program version")
        .addOption("s", "server", false, "start server on specified address")
        .addOption("c", "console", false, "open console on specified address")

      OptionBuilder.withLongOpt("address")
      OptionBuilder.withDescription("connect/create server on specified address [ip:port]")
      OptionBuilder.hasArgs(2)
      OptionBuilder.withValueSeparator(':')
      OptionBuilder.withArgName("IP_ADDRESS:PORT")
      options.addOption(OptionBuilder.create("a"))

      OptionBuilder.withLongOpt("import")
      OptionBuilder.withDescription("import metadata from given item")
      OptionBuilder.hasArg
      OptionBuilder.isRequired(false)
      OptionBuilder.withArgName("ITEM_ROOT")
      options.addOption(OptionBuilder.create("i"))

      try {
        val line = parser.parse(options, args)

        if (line.hasOption("h")) (new HelpFormatter()).printHelp("trigram", options)
        else if (line.hasOption("v")) println(getVersion)
        else if (line.hasOption("i")) ModelFactory.load(line.getOptionValue("i"))
        else {
          val defaultAddress = Array("localhost", "10001")
          val address = if (line.hasOption("a")) line.getOptionValues("a") else defaultAddress

          if (line.hasOption("s")) Server.run(address)
          else if (line.hasOption("c")) Console.run(address)
        }
      } catch {
        case exp: ParseException => println("ParseException:" + exp.getMessage)
      }
    }

}
