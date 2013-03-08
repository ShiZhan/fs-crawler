/**
 * @author ShiZhan
 * 2013
 * TriGraM main program
 */
import org.apache.commons.cli._
import core.{ Server, Console }
import core.ModelFactory.importData
import util.{ Logging, Version }

object trigram extends Logging {

  def main(args: Array[String]) {

    println("Triple Graph based Metadata storage - TriGraM")

    if (args.length == 0) {
      println("Use '-h' option to access help")
      return
    }

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

    OptionBuilder.withLongOpt("initialize")
    OptionBuilder.withDescription("import metadata from given item")
    OptionBuilder.hasArg
    OptionBuilder.isRequired(false)
    OptionBuilder.withArgName("ITEM_ROOT")
    options.addOption(OptionBuilder.create("i"))

    val defaultAddress = Array("localhost", "10001")

    try {
      val line = parser.parse(options, args)

      if (line.hasOption("h")) (new HelpFormatter()).printHelp("trigram", options)

      if (line.hasOption("v")) println(Version.getVersion)

      if (line.hasOption("i")) importData(line.getOptionValue("i"))

      val address = if (line.hasOption("a")) line.getOptionValues("a")
      else defaultAddress

      if (line.hasOption("s")) Server.run(address)
      else if (line.hasOption("c")) Console.run(address)
    } catch {
      case exp: ParseException =>
        logger.warn("Unexpected exception:" + exp.getMessage())
    }
  }

}
