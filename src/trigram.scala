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
        options.addOption("a", "all", false, "do not hide entries starting with .")
        options.addOption("A", "almost-all", false, "do not list implied . and ..")
        options.addOption("b", "escape", false, "print octal escapes for nongraphic "
                                             + "characters")

        OptionBuilder.withLongOpt("block-size")
        OptionBuilder.withDescription("use SIZE-byte blocks")
        OptionBuilder.hasArg
        OptionBuilder.withArgName("SIZE")

        options.addOption(OptionBuilder.create())

        options.addOption("B", "ignore-backups", false, "do not list implied entried "
                                                     + "ending with ~")
        options.addOption("c", false, "with -lt: sort by, and show, ctime (time of last " 
                                   + "modification of file status information) with "
                                   + "-l:show ctime and sort by name otherwise: sort "
                                   + "by ctime")
        options.addOption("C", false, "list entries by columns")

        //val args: Array[String] = Array("--block-size=10")

        try {
            // parse the command line arguments
            val line: CommandLine = parser.parse( options, args )
            
            if(line.hasOption("help")) {
                // automatically generate the help statement
                val formatter = new HelpFormatter();
                formatter.printHelp("trigram", options);
            }
    
            // validate that block-size has been set
            if(line.hasOption("block-size")) {
                // print the value of block-size
                System.out.println( line.getOptionValue( "block-size" ) )
            }
    
        }
        catch {
            case exp: ParseException =>
                System.out.println( "Unexpected exception:" + exp.getMessage())
        }


    }
}
