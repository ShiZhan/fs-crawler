/**
 * @author ShiZhan
 * @year 2012~2014
 * @name TriGraM Project
 */
object Trigram {
  import kernel.{ Console, Engine }
  import modeler.Modelers

  val usage = """usage: Trigram
 no argument     enter console
 -h              print this message
 -v              show program version
 -i [MODEL ...]            import model(s)
 -c [MODEL ...]            combine model(s)
 -q [SPARQL ...]           http://www.w3.org/TR/sparql11-query/
 -u [SPARQL ...]           http://www.w3.org/TR/sparql11-update/
 -R [MODEL] <Rules ...>    Infer over [MODEL] using <Rules ...>
 -m [modeler] <args ...>   Use [modeler] with arguments:
""" + Modelers.help
  val incorrectArgs = "Incorrect parameters, see help (trigram -h)."

  def main(args: Array[String]) = {
    println("Triple Graph based Metadata storage - TriGraM")
    args.toList match {
      case Nil => Console.run
      case "-h" :: Nil => println(usage)
      case "-v" :: Nil => println(helper.Version.get)
      case "-i" :: modelFNs => modelFNs.foreach(Engine.tdbloader)
      case "-c" :: modelFNs => if (modelFNs.length > 1) Engine.combine(modelFNs)
      case "-q" :: qArgs => Engine.doQuery(qArgs)
      case "-u" :: uArgs => Engine.doUpdate(uArgs)
      case "-R" :: modelFN :: ruleFNs => Engine.infer(modelFN, ruleFNs)
      case "-m" :: modeler :: mArgs =>
        println(s"run modeler [$modeler] with options [$mArgs]")
        Modelers.run(modeler, mArgs)
      case _ => println(incorrectArgs)
    }
  }
}