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
   help                       print this message
   version                    show program version
   interpreters               show available interpreters
   interpreter:: <operation>  do "Domain Specific Command"
                              indicated by "interpreter::"
   exit                       exit console
"""

  private val consoleTitle = "TriGraM Console"
  private val consolePrompt = "# "

  def run(): Unit = {
    println(consoleTitle)
    print(consolePrompt)

    for (line <- io.Source.stdin.getLines) {
      line.split("::").toList match {
        case "exit" :: Nil => return

        case "help" :: Nil => println(consoleUsage)
        case "version" :: Nil => println(Version.getVersion)

        case "test" :: Nil => println("WIP")

        case "interpreters" :: Nil => println(Interpreter.help)

        case prefix :: cmd :: Nil =>
          println("Do OP: " + Interpreter.get(prefix)(cmd))

        case "" :: Nil => {}

        case _ => println("Unrecognized command: " + line +
          "\nUse 'help' to list available commands")
      }

      print(consolePrompt)
    }
  }

}
