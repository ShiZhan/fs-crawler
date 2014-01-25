/**
 * @author ShiZhan
 * @year 2012~2014
 * @name TriGraM Project
 */
object Trigram {
  import kernel.Engine
  import modeler.Modelers

  val usage = """
usage: Trigram
 -h                        print this message
 -v                        show program version
 -m [modeler] <args ...>   Use [modeler] with arguments:

""" + Modelers.help

  val incorrectArgs = "Incorrect parameters, see help (trigram -h)."

  def main(args: Array[String]) = {
    println("Triple Graph based Metadata storage - TriGraM")

    args.toList match {
      case "-h" :: Nil => println(usage)
      case "-v" :: Nil => println(helper.Version.get)
      case "-R" :: modelFN :: ruleFNs => Engine.infer(modelFN, ruleFNs)
      case "-m" :: modeler :: mArgs => {
        println("run modeler [%s] with options [%s]".format(modeler, mArgs))
        Modelers.run(modeler, mArgs)
      }
      case _ => println(incorrectArgs)
    }
  }
}