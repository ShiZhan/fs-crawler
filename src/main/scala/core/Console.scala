/**
 * Console Application
 */
package core

import com.hp.hpl.jena.Jena
import com.hp.hpl.jena.tdb.TDB
import util.Version

/**
 * @author ShiZhan
 * 2013
 * Console command loop
 * main entry to Command Line Interface
 */
object Console {

  private val store = Store()
  private val handler = Handler(store)

  private val consoleUsage = """ [Console Usage]
  help               print this message
  status             show program status
  modes              list available command modes
  mode <mode>        enter <mode> to execute specific commands
  exit               exit console"""

  private val consoleTitle = "TriGraM Console"
  private val consolePrompt = "# "

  def run: Unit = {
    println(consoleTitle)
    print(consolePrompt)

    for (line <- io.Source.stdin.getLines) {
      val output = line.split(" ").toList match {
        case "exit" :: Nil =>
          store.close; return

        case "help" :: Nil => consoleUsage
        case "status" :: Nil =>
          "trigram version :  " + Version.get + "\n" +
            "Jena core version: " + Jena.VERSION + "\n" +
            "Jena core build:   " + Jena.BUILD_DATE + "\n" +
            "Jena TDB version:  " + TDB.VERSION + "\n" +
            "Jena TDB build:    " + TDB.BUILD_DATE + "\n" +
            "data location:     " + store.location

        case "test" :: Nil => "internal test command"

        case "modes" :: Nil => handler.help
        case "mode" :: mode :: Nil =>
          handler.enterCLI(mode)
          "return to default console"

        case "" :: Nil => null

        case _ => "Unrecognized command: [%s]".format(line)
      }

      if (output != null) println(output)

      print(consolePrompt)
    }
  }

}