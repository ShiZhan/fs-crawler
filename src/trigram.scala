/**
 * @author ShiZhan
 * 2013
 * TriGraM main program
 */
import org.apache.commons.cli._
import server.Server
import client.Console
import model.Model
import util._

object trigram extends Logging {

	val defaultAddress = "127.0.0.1:10001"

  def main(args: Array[String]) {

  	println("Triple Graph based Metadata storage - TriGraM")    

    // create the command line parser
    val parser: CommandLineParser = new PosixParser()

    // create the Options
    val options = new Options()
      .addOption("h", "help", false, "print this message" )
      .addOption("v", "version", false, "show program version")
      .addOption("s", "server", false, "start server on specified address")
      .addOption("c", "client", false, "open console on specified address")

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

    try {
      // parse the command line arguments
      val line: CommandLine = parser.parse(options, args)

      if(line.hasOption("h")) {
        // automatically generate the help statement
        (new HelpFormatter()).printHelp("trigram", options)
      }

      if(line.hasOption("v")) println(Version.getVersion)

      if(line.hasOption("i")) Model.importFromRoot(line.getOptionValue("i"))

      val address = if(line.hasOption("a")) line.getOptionValue("a")
                    else defaultAddress

      if(line.hasOption("s")) Server.run(address)
      else if(line.hasOption("c")) Console.run(address)
    }
    catch {
      case exp: ParseException =>
        logger.warn( "Unexpected exception:" + exp.getMessage())
    }
  }

}
