/**
 * Console Application
 */
package core

import util.Version

/**
 * @author ShiZhan
 * 2013
 * Console command loop with Command handler and Store
 */
object Console extends Handler {

  private val consoleUsage = """
  [Console Usage]
   help                   print this message
   version                show program version
   handlers               show available command parsers
   handler:: <operation>  do "Domain Specific Command"
                          indicated by "parser::"
   exit                   exit console
"""

  private val consoleTitle = "TriGraM Console"
  private val consolePrompt = "# "

  def run: Unit = {
    println(consoleTitle)
    print(consolePrompt)

    for (line <- io.Source.stdin.getLines) {
      line.split("::").toList match {
        case "exit" :: Nil =>
          close
          return

        case "help" :: Nil => println(consoleUsage)
        case "version" :: Nil => println(Version.getVersion)

        case "test" :: Nil => println("internal test command")

        case "handlers" :: Nil => println(help)
        case handler :: cmd :: Nil => println(getHandler(handler)(cmd))

        case "" :: Nil => {}

        case _ => println("Unrecognized command: " + line +
          "\nUse 'help' to list available commands")
      }

      print(consolePrompt)
    }
  }

}
