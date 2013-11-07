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

  private val usage = """ [Console Usage]
  help           print this message
  status         show program status
  query          enter SPARQL to do query
  update         enter SPARQL to do update
  exit           exit console"""

  private val title = "TriGraM Console"
  private val prompt = "# "

  private val status = {
    import util.Platform.{ os, javaVer, scalaVer }
    import com.hp.hpl.jena.Jena.{ VERSION => jenaVer, BUILD_DATE => jenaBuild }
    import com.hp.hpl.jena.tdb.TDB.{ VERSION => tdbVer, BUILD_DATE => tdbBuild }
    val tgmVer = util.Version.get
    val tgmRoot = util.Config.TGMROOT
    val tgmData = new java.io.File(store.location).getAbsoluteFile
    s"""TriGraM:   $tgmVer
  code:    $tgmRoot
  data:    $tgmData
Jena core: $jenaVer $jenaBuild
Jena TDB:  $tdbVer $tdbBuild
Scala:     $scalaVer
Java:      $javaVer
OS:        $os"""
  }

  def run: Unit = {
    println(title)
    print(prompt)

    for (line <- io.Source.stdin.getLines) {
      val output = line.split(" ").toList match {
        case "exit" :: Nil =>
          store.close; return

        case "help" :: Nil => usage
        case "status" :: Nil => status

        case "test" :: Nil => "internal test command"

        case "query" :: Nil => handler.doQuery
        case "update" :: Nil => handler.doUpdate

        case "" :: Nil => null

        case _ => "Unrecognized command: [%s]".format(line)
      }

      if (output != null) println(output)

      print(prompt)
    }
  }

}
