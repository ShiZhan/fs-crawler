import org.apache.commons.cli._

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
    OptionBuilder.withArgName("ROOT_DIR")

    options.addOption(OptionBuilder.create("i"))

    OptionBuilder.withLongOpt("command")
    OptionBuilder.withDescription("run command line on specified server")
    OptionBuilder.hasArg
    OptionBuilder.withArgName("COMMAND")

    options.addOption(OptionBuilder.create("c"))


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

      if(line.hasOption("i")) {
        System.out.println(line.getOptionValue("i"))
      }

      if(line.hasOption("c")) {
        System.out.println("Command: " + line.getOptionValue("c"))
      }

    }
    catch {
      case exp: ParseException =>
        System.out.println( "Unexpected exception:" + exp.getMessage())
    }


  }
}
