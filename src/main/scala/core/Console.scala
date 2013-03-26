/**
 * Console program
 */
package core

import util.Version

/**
 * @author ShiZhan
 * 2013
 * Console command loop
 */
object Console {

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
  private val consolePrompt = "> "

  def run(): Unit = {
    println(consoleTitle)
    print(consolePrompt)

    for (line <- io.Source.stdin.getLines) {
      line.split(" ").toList match {
        case "exit" :: Nil => return

        case "help" :: Nil => println(consoleUsage)
        case "version" :: Nil => println(Version.getVersion)

        case "test" :: Nil => println("WIP")

        case "put" :: file => println("Uploading: " + file)
        case "get" :: file => println("Downloading: " + file)
        case "mv" :: origin :: target => println("Move: " + origin + " to: " + target)
        case "mk" :: directory => println("Create directory: " + directory)
        case "cd" :: directory => println("Change to: " + directory)
        case "ls" :: directory => println("Content of: " + directory)
        case "rm" :: target => println("Removing: " + target)

        case "" :: Nil => {}

        case _ => println("Unrecognized command: " + line +
          "\nUse 'help' to list available commands")
      }

      print(consolePrompt)
    }
  }

}
