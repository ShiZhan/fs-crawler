/**
 *
 */
package client

import util.{Logging, Version}

/**
 * @author ShiZhan
 * 2013
 * Program console
 */
object Console extends Logging {
	
	val consoleUsage = 
		"[Console Usage]\n" +
		" help                  print this message\n" +
		" version               show program version\n" +
    " put <file>            upload a file\n" +
    " get <file>            download a file\n" +
    " mv <origin> <target>  move/rename a file/directory (end with '/')\n" +
    " mk <directory>        create a directory\n" +
    " cd <directory>        change current directory\n" +
    " ls <directory>        list directory content\n" +
    " rm <target>           remove file or directory\n" +
		" exit                  exit console\n"

  val consoleTitle = "TriGraM console"
  val consolePrompt = " > "

	def run(address: String) {
		logger.info("Opening CLI on " + address)

		println(consoleTitle)
		System.out.print(address + consolePrompt)

  	for(line <- io.Source.stdin.getLines) {
      line.split(' ') match {
        case Array("exit") => return

        case Array("help") => println(consoleUsage)
        case Array("version") => println(Version.getVersion)

        case Array("put", file) => println("Uploading: " + file)
        case Array("get", file) => println("Downloading: " + file)
        case Array("mv", origin, target) => println("Move: " + origin + " to: " + target)
        case Array("mk", directory) => println("Create directory: " + directory)
        case Array("cd", directory) => println("Change to: " + directory)
        case Array("ls", directory) => println("Content of: " + directory)
        case Array("rm", target) => println("Removing: " + target)

        case _ => println("Unrecognized command: " + line +
                          "\nUse 'help' to list available commands")
      }

    	System.out.print(address + consolePrompt)
    }
	}

}
