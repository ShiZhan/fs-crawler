/**
 * @author ShiZhan
 * @year 2012~2014
 * @name TriGraM Project
 */
object Trigram {
  import kernel.{ Console, Engine, Merger }
  import modeler.Modelers
  import cim.Schema

  val usage = """
usage: Trigram
 -h              print this message
 -v              show program version
 <no argument>   enter console

 database:
 -i [MODEL ...]  import model
 -q [SPARQL ...] http://www.w3.org/TR/sparql11-query/
 -u [SPARQL ...] http://www.w3.org/TR/sparql11-update/

 model operations:
 -s <CIM Schema XML>       Update CIM schema as TBox for modelers
                           default CIM Schema is packaged within
 -C <CIM classes>          Check CIM Schema & vocabulary availability
 -g [output] <CIM classes> Gather selected CIM classes into [output] as TBox
 -c [MODEL ...]            Combine multiple models
 -R [MODEL] <Rules ...>    Infer over [MODEL] using <Rules ...>

 modeler:
 -m MODELER <args...>      Use [modeler] with arguments:

""" + Modelers.help

  val incorrectArgs = "Incorrect parameters, see help (trigram -h)."

  def main(args: Array[String]) = {
    println("Triple Graph based Metadata storage - TriGraM")

    args.toList match {
      case Nil => Console.run
      case "-h" :: Nil => println(usage)
      case "-v" :: Nil => println(helper.Version.get)
      case "-i" :: modelFNs => modelFNs.foreach(Engine.tdbloader)
      case "-q" :: qArgs => Engine.doQuery(qArgs)
      case "-u" :: uArgs => Engine.doUpdate(uArgs)
      case "-s" :: cimXML => Schema.fromXML(cimXML).toModelGroup
      case "-s1" :: cimXML => Schema.fromXML(cimXML).toModel
      case "-C" :: cArgs => Schema.check(cArgs)
      case "-g" :: output :: selected => Merger.gather(output, selected)
      case "-c" :: modelFNs =>
        if (modelFNs.size > 1) {
          Merger.combine(modelFNs)
          println(modelFNs.size + " models combined.")
        } else println("There's only one model out there.")
      case "-R" :: modelFN :: ruleFNs => Engine.inferWithRule(modelFN, ruleFNs)
      case "-m" :: modeler :: mArgs => {
        println("run modeler [%s] with options [%s]".format(modeler, mArgs))
        Modelers.run(modeler, mArgs)
      }
      case _ => println(incorrectArgs)
    }
  }
}