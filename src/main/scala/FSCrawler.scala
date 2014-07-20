/**
 * @author ShiZhan
 * @year 2012~2014
 * @name TriGraM Project
 */
object FSCrawler extends App {
  import modeler.Modelers

  val usage = """usage: FSCrawler
 -h   print this message
 -v   show program version
 -m [modeler] <args ...>   Use [modeler] with arguments:
""" + Modelers.help
  val incorrectArgs = "Incorrect parameters, see help (FSCrawler -h)."

  println("File System Crawler")
  args.toList match {
    case "-h" :: Nil => println(usage)
    case "-v" :: Nil => println(helper.Version.get)
    case "-m" :: modeler :: mArgs => Modelers.run(modeler, mArgs)
    case _ => println(incorrectArgs)
  }
}