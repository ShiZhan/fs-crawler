import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.OptionBuilder
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import org.apache.commons.cli.PosixParser

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
    options.addOption("c", "client", false, "open command line interface on specified server")
    options.addOption("i", "initialize", false, "initialize models")

    OptionBuilder.withLongOpt("address")
    OptionBuilder.withDescription("connect/create server on specified ip address")
    OptionBuilder.hasArg
    OptionBuilder.withArgName("IP_ADDRESS")

    options.addOption(OptionBuilder.create("a"))

    OptionBuilder.withLongOpt("port")
    OptionBuilder.withDescription("connect/listen to specified port")
    OptionBuilder.hasArg
    OptionBuilder.withArgName("PORT")

    options.addOption(OptionBuilder.create("p"))

    OptionBuilder.withLongOpt("root")
    OptionBuilder.withDescription("specify root directory" +
    		" (use current directory as default) for creating node model")
    OptionBuilder.hasArg
    OptionBuilder.withArgName("ROOT_DIR")

    options.addOption(OptionBuilder.create("r"))

    OptionBuilder.withLongOpt("model")
    OptionBuilder.withDescription("load server on specified model file (default: node.owl)")
    OptionBuilder.hasArg
    OptionBuilder.withArgName("MODEL_FILE")

    options.addOption(OptionBuilder.create("m"))


    try {
      // parse the command line arguments
      val line: CommandLine = parser.parse(options, args)
      
      if(line.hasOption("h")) {
        // automatically generate the help statement
        val formatter = new HelpFormatter();
        formatter.printHelp("trigram", options);
      }

      if(line.hasOption("a")) {
        System.out.println("IP address: " + line.getOptionValue("a"))
      }

      if(line.hasOption("p")) {
        System.out.println(line.getOptionValue("p"))
      }

      if(line.hasOption("r")) {
        System.out.println(line.getOptionValue("r"))
      }

      if(line.hasOption("m")) {
        System.out.println(line.getOptionValue("m"))
      }

    }
    catch {
      case exp: ParseException =>
        System.out.println( "Unexpected exception:" + exp.getMessage())
    }


  }
}
