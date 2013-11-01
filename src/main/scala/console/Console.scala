/**
 * Console Application
 */
package console

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
  help           print this message
  status         show program status
  query          enter SPARQL to do query
  update         enter SPARQL to do update
  exit           exit console"""

  private val consoleTitle = "TriGraM Console"
  private val consolePrompt = "# "

  private val status =
    "Java:      " + System.getProperty("java.version") + "\n" +
      "Scala:     " + scala.util.Properties.versionMsg + "\n" +
      "TriGraM:   " + util.Version.get + "\n" +
      "  code:    " + util.Config.TGMROOT + "\n" +
      "  data:    " + new java.io.File(store.location).getAbsoluteFile + "\n" +
      "Jena core: " + com.hp.hpl.jena.Jena.VERSION +
      " " + com.hp.hpl.jena.Jena.BUILD_DATE + "\n" +
      "Jena TDB:  " + com.hp.hpl.jena.tdb.TDB.VERSION +
      " " + com.hp.hpl.jena.tdb.TDB.BUILD_DATE + "\n"

  def run: Unit = {
    println(consoleTitle)
    print(consolePrompt)

    for (line <- io.Source.stdin.getLines) {
      val output = line.split(" ").toList match {
        case "exit" :: Nil =>
          store.close; return

        case "help" :: Nil => consoleUsage
        case "status" :: Nil => status

        case "test" :: Nil => "internal test command"

        case "query" :: Nil => handler.doQuery
        case "update" :: Nil => handler.doUpdate

        case "" :: Nil => null

        case _ => "Unrecognized command: [%s]".format(line)
      }

      if (output != null) println(output)

      print(consolePrompt)
    }
  }

}
