/**
 * @author ShiZhan
 * 2013
 * TriGraM main program
 */
import org.apache.commons.cli._
import core.{ Server, Console }
import model.Model.importFromRoot
import util._

object trigram extends Logging {

  val defaultAddress = Array("localhost", "10001")

  def main(args: Array[String]) {

    println("Triple Graph based Metadata storage - TriGraM")

    // create the command line parser
    val parser: CommandLineParser = new PosixParser()

    // create the Options
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
    OptionBuilder.withDescription("create model on given root directory")
    OptionBuilder.hasArg
    OptionBuilder.isRequired(false)
    OptionBuilder.withArgName("ROOT_DIR")
    options.addOption(OptionBuilder.create("i"))

    try {
      // parse the command line arguments
      val line: CommandLine = parser.parse(options, args)

      if (line.hasOption("h")) (new HelpFormatter()).printHelp("trigram", options)

      if (line.hasOption("v")) println(Version.getVersion)

      if (line.hasOption("i")) importFromRoot(line.getOptionValue("i"))

      val address = if (line.hasOption("a")) line.getOptionValues("a")
      else defaultAddress

      if (line.hasOption("s")) new Server(address).run
      else if (line.hasOption("c")) new Console(address).run
    } catch {
      case exp: ParseException =>
        logger.warn("Unexpected exception:" + exp.getMessage())
    }
  }

}
