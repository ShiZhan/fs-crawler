/**
 *
 */
package core

import util.{ Logging, Version }

/**
 * @author ShiZhan
 * 2013
 * Program console
 */
object Console extends Logging {

  private val consoleUsage = """
  [Console Usage]
   help                  print this message
   version               show program version
   put <file>            upload a file
   get <file>            download a file
   mv <origin> <target>  move/rename a file/directory (end with '/')
   mk <directory>        create a directory
   cd <directory>        change current directory
   ls <directory>        list directory content
   rm <target>           remove file or directory
   exit                  exit console
"""

  private val consoleTitle = "TriGraM Console"
  private val consolePromptChar = " > "

  def run(address: Array[String]): Unit = {
    logger.info("Opening CLI on " + address.mkString(":"))

    val consolePrompt = address.mkString(":") + consolePromptChar

    println(consoleTitle)
    print(consolePrompt)

    val connection = Client.getConnection(address)

    for (line <- io.Source.stdin.getLines) {
      line.split(' ') match {
        case Array("exit") => {
          Client.shutdown
          return
        }

        case Array("help") => println(consoleUsage)
        case Array("version") => println(Version.getVersion)

        case Array("test") => println(connection.deliver(line))

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

      print(consolePrompt)
    }
  }

}
