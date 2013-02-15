import org.apache.commons.cli._
import com.hp.hpl.jena.sparql.core._
import jena._
import com.hp.hpl.jena.tdb.{ TDB, TDBFactory } 

object trigram {

  def main(args: Array[String]) {
    println("Triple Graph based Metadata storage - TriGraM")    

    // create the command line parser
    val parser: CommandLineParser = new PosixParser()

    // create the Options
    val options = new Options()
    options.addOption("h", "help", false, "print this message" );
    options.addOption("v", "version", false, "print the version information and exit")
    options.addOption("s", "server", false, "start server on specified ip and port")

    OptionBuilder.withLongOpt("address")
    OptionBuilder.withDescription("connect/create server on specified ip address:port")
    OptionBuilder.hasArg
    OptionBuilder.withArgName("IP_ADDRESS:PORT")
    options.addOption(OptionBuilder.create("a"))

    OptionBuilder.withLongOpt("initialize")
    OptionBuilder.withDescription("specify root directory" +
    		" (use current directory as default) for creating node model")
    OptionBuilder.hasArg
    OptionBuilder.isRequired(false)
    OptionBuilder.withArgName("ROOT_DIR")
    options.addOption(OptionBuilder.create("i"))

    OptionBuilder.withLongOpt("command")
    OptionBuilder.withDescription("run command line on specified server")
    OptionBuilder.hasArg
    OptionBuilder.withArgName("COMMAND")
    options.addOption(OptionBuilder.create("c"))

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
        println("WIP")
      }

      if(line.hasOption("a")) {
        address = line.getOptionValue("a")
      }

      if(line.hasOption("i")) {
      	var root_dir = line.getOptionValue("i")
        println("initialize model with root directory: " + root_dir)
      }

      if(line.hasOption("c")) {
        println("Command: " + line.getOptionValue("c"))
      }

      if(line.hasOption("s")) {
        // get version information
        println("Starting server on " + address)
      }

    }
    catch {
      case exp: ParseException =>
        println( "Unexpected exception:" + exp.getMessage())
    }


  }
}
