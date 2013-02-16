/**
 * @author ShiZhan
 * 2013
 * TriGraM main program
 */
import org.apache.commons.cli._
import model._
import util._

object trigram extends Logging {

  def main(args: Array[String]) {
    println("Triple Graph based Metadata storage - TriGraM")    

    // create the command line parser
    val parser: CommandLineParser = new PosixParser()

    // create the Options
    val options = new Options()
    options.addOption("h", "help", false, "print this message" );
    options.addOption("v", "version", false, "show program version")
    options.addOption("s", "server", false, "start server on specified address")
    options.addOption("c", "client", false, "open console on specified address")

    OptionBuilder.withLongOpt("address")
    OptionBuilder.withDescription("connect/create server on specified address [ip:port]")
    OptionBuilder.hasArg
    OptionBuilder.withArgName("IP_ADDRESS:PORT")
    options.addOption(OptionBuilder.create("a"))

    OptionBuilder.withLongOpt("initialize")
    OptionBuilder.withDescription("create model on given root directory")
    OptionBuilder.hasArg
    OptionBuilder.isRequired(false)
    OptionBuilder.withArgName("ROOT_DIR")
    options.addOption(OptionBuilder.create("i"))

    var address = "127.0.0.1:10001"

    try {
      // parse the command line arguments
      val line: CommandLine = parser.parse(options, args)
      
      if(line.hasOption("h")) {
        // automatically generate the help statement
        val formatter = new HelpFormatter();
        formatter.printHelp("trigram", options);
      }

      if(line.hasOption("v")) {
        // get version information
        println(Version.getVersion())
      }

      if(line.hasOption("a")) {
        address = line.getOptionValue("a")
      }

      if(line.hasOption("i")) {
      	importModel(line.getOptionValue("i"))
      }

      if(line.hasOption("s")) {
        // get version information
        logger.info("Starting server on " + address)
      }
      else if(line.hasOption("c")) {
        // get version information
        logger.info("Opening CLI on " + address)
      }

    }
    catch {
      case exp: ParseException =>
        logger.warn( "Unexpected exception:" + exp.getMessage())
    }
  }
}
