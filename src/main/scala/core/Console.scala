/**
 * Console program
 */
package core

import util.{ Logging, Version }

/**
 * @author ShiZhan
 * 2013
 * Console command loop
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
      line.split(" ").toList match {
        case "exit" :: Nil => {
          Client.shutdown
          return
        }

        case "help" :: Nil => println(consoleUsage)
        case "version" :: Nil => println(Version.getVersion)

        case "test" :: Nil => println(connection.deliver(line))

        case "put" :: file => println("Uploading: " + file)
        case "get" :: file => println("Downloading: " + file)
        case "mv" :: origin :: target => println("Move: " + origin + " to: " + target)
        case "mk" :: directory => println("Create directory: " + directory)
        case "cd" :: directory => println("Change to: " + directory)
        case "ls" :: directory => println("Content of: " + directory)
        case "rm" :: target => println("Removing: " + target)

        case _ => println("Unrecognized command: " + line +
          "\nUse 'help' to list available commands")
      }

      print(consolePrompt)
    }
  }

}
