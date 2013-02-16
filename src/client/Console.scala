/**
 *
 */
package client

import org.apache.commons.cli._

import util.Logging

/**
 * @author ShiZhan
 *
 */
object Console extends Logging {

	def run(address: String) {
		logger.info("Opening CLI on " + address)
		
		println("TriGraM shell")

		val parser: CommandLineParser = new PosixParser()
		val options = new Options()
    options.addOption("h", "help", false, "print this message" );
    options.addOption("v", "version", false, "show program version")

		var runningFlag = true

//		do {
//      try {
//        // parse the command line arguments
//        val line: CommandLine = parser.parse(options, System.in.read())
//  
//        if(line.hasOption("h")) {
//        }
//  
//        if(line.hasOption("exit")) runningFlag = false
//
//      }
//      catch {
//        case exp: ParseException =>
//          logger.warn( "Unrecognized command:" + exp.getMessage())
//      }
//		} while (runningFlag)
	}

}