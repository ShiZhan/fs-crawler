/**
 * @author ShiZhan
 * @year 2012~2014
 * @name TriGraM Project
 */
object Trigram {
  import kernel.{ Console, Engine }
  import modeler.{ Modelers, Merger }
  import cim.Schema

  val usage = """
usage: Trigram
 -h             print this message
 -v             show program version
 <no argument>  enter console

 database:
 -i [MODEL...]  import model
 -q [SPARQL]    http://www.w3.org/TR/sparql11-query/
 -u [SPARQL]    http://www.w3.org/TR/sparql11-update/

 model operations:
 -s <CIM Schema XML>       Update CIM schema as TBox for modelers
                           default CIM Schema is packaged within
 -C                        Check CIM Schema and vocabulary
 -g [output] <CIM classes> Gather selected CIM classes into [output] as TBox
                           Based on the OWL.imports in <CIM classes>
 -c [MODEL...]             Combine multiple models
 -I [DATA] [CIM] [output]  Infer over [DATA] using [CIM] as schema
 -R [DATA] [Rule] [output] Infer over [DATA] using [Rule]

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
      case "-i" :: modelFiles => modelFiles.foreach(Engine.tdbloader)
      case "-q" :: qArgs => Engine.doQuery(qArgs)
      case "-u" :: uArgs => Engine.doUpdate(uArgs)
      case "-s" :: sArgs => Schema.fromXML(sArgs).toModelGroup
      case "-s1" :: sArgs => Schema.fromXML(sArgs).toModel
      case "-C" :: cArgs => Schema.check(cArgs)
      case "-g" :: output :: Nil => Merger.gather(output, Modelers.tbox.toList)
      case "-g" :: output :: selected => Merger.gather(output, selected)
      case "-c" :: modelFiles =>
        if (modelFiles.size > 1) {
          Merger.combine(modelFiles)
          println(modelFiles.size + " models combined.")
        } else println("There's only one model out there.")
      case "-I" :: iArgs => Engine.inferWithOWL(iArgs)
      case "-R" :: rArgs => Engine.inferWithRule(rArgs)
      case "-m" :: modeler :: mArgs => {
        println("invoking [%s] modeler with options [%s]"
          .format(modeler, mArgs.mkString(" ")))
        Modelers.run(modeler, mArgs.toArray)
      }
      case _ => println(incorrectArgs)
    }
  }
}