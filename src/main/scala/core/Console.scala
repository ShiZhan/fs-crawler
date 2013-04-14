/**
 * Console Application
 */
package core

import util.Version

/**
 * @author ShiZhan
 * 2013
 * Console command loop
 * main entry to Domain Specific Command Line Interface
 */
object Console extends Handler(Store.defaultLocation) {

  private val consoleUsage = """ [Console Usage]
  help               print this message
  version            show program version
  modes              list available command modes
  mode <mode>        enter <mode> to execute "Domain Specific Command"
  exit               exit console"""

  private val consoleTitle = "TriGraM Console"
  private val consolePrompt = "# "

  def run: Unit = {
    println(consoleTitle)
    print(consolePrompt)

    for (line <- io.Source.stdin.getLines) {
      val output = line.split(" ").toList match {
        case "exit" :: Nil =>
          close; return

        case "help" :: Nil => consoleUsage
        case "version" :: Nil => Version.get

        case "test" :: Nil => "internal test command"

        case "modes" :: Nil => help
        case "mode" :: mode :: Nil =>
          enterDSCLI(mode)
          "return to default console"

        case "" :: Nil => null

        case _ => "Unrecognized command: [%s]".format(line)
      }

      if (output != null) println(output)

      print(consolePrompt)
    }
  }

}
