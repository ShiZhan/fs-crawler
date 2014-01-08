/**
 * Console command loop
 */
package console

/**
 * @author ShiZhan
 * 2013
 * Console command loop
 */
object Console {
  import util.Config.{ TGMROOT, TGMDATA, CIMDATA }
  import util.Platform.BRIEFING

  private val loc = TGMDATA
  private val store = Store(loc)

  private val usage = """ [Console Usage]
  help           print this message
  status         show program status
  query          enter SPARQL to do query
  update         enter SPARQL to do update
  exit           exit console"""
  private val title = "TriGraM Console"
  private val prompt = "# "

  private val TGMVER = util.Version.get
  private val status = s"""
TriGraM:     $TGMVER
  code:      $TGMROOT
  data:      $loc
  CIM:       $CIMDATA""" + BRIEFING

  private def typeInput = {
    println("input below, end with Ctrl+E.")
    io.Source.fromInputStream(System.in).takeWhile(_ != 5.toChar).mkString
  }

  private def fileInput(fileName: String) = {
    try {
      io.Source.fromFile(fileName).mkString
    } catch {
      case e: Exception => e.printStackTrace; ""
    }
  }

  def run: Unit = {
    println(title)
    print(prompt)

    for (line <- io.Source.stdin.getLines) {
      line.split(" ").toList match {
        case "exit" :: Nil =>
          store.close; return
        case "help" :: Nil => println(usage)
        case "status" :: Nil => println(status)
        case "time" :: Nil => println(util.DateTime.get)
        case "tdbinfo" :: Nil => tdb.tdbstats.main("--loc=" + loc)
        case "query" :: Nil => store.doQuery(typeInput)
        case "update" :: Nil => store.doUpdate(typeInput)
        case "query" :: sqlFile :: Nil => store.doQuery(fileInput(sqlFile))
        case "update" :: sqlFile :: Nil => store.doUpdate(fileInput(sqlFile))

        case _ => "Unrecognized command: [%s]".format(line)
      }

      print(prompt)
    }
  }
}