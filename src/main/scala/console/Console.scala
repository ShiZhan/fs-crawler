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

  private val loc = util.Config.TGMDATA
  private val store = new Store(loc)
  private val handler = new Handler(store)

  private val usage = """ [Console Usage]
  help           print this message
  status         show program status
  query          enter SPARQL to do query
  update         enter SPARQL to do update
  exit           exit console"""
  private val title = "TriGraM Console"
  private val prompt = "# "

  private val status = {
    import com.hp.hpl.jena.Jena.{ VERSION => JENAVER, BUILD_DATE => JENABUILD }
    import com.hp.hpl.jena.tdb.TDB.{ VERSION => TDBVER, BUILD_DATE => TDBBUILD }
    import util.Config.{ TGMROOT, CIMDATA }
    import util.Platform.{ HOSTNAME, OS, JAVAVER, SCALAVER }
    import util.JVMstates.{ MEMFREE, MEMUSED, MEMTOTAL, MEMMAX }
    val TGMVER = util.Version.get
    s"""
TriGraM:     $TGMVER
  code:      $TGMROOT
  data:      $loc
  CIM:       $CIMDATA
Jena core:   $JENAVER $JENABUILD
Jena TDB:    $TDBVER $TDBBUILD
Scala:       $SCALAVER
Java:        $JAVAVER
  MEM FREE:  $MEMFREE MB
  MEM USED:  $MEMUSED MB
  MEM TOTAL: $MEMTOTAL MB
  MEM MAX:   $MEMMAX MB
OS:          $OS
HOSTNAME:    $HOSTNAME
"""
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
        case "time" :: Nil => util.DateTime.get
        case "tdbinfo" :: Nil =>
          tdb.tdbstats.main("--loc=" + loc); null

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