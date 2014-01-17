/**
 * @author ShiZhan
 * @year 2012~2014
 * @name TriGraM Project
 */
object Trigram {
  import kernel.{ Console, Engine }
  import modeler.{ Modelers, Merger }
  import cim.Schema
  import helper.{ GetString, Version }

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
 -s [CIM Schema XML]       Update CIM schema as TBox for modelers
                           [CIM Schema XML]: http://dmtf.org/standards/cim
 -g [output] <CIM classes> Gather selected CIM classes into [output] as TBox
                           Based on the OWL.imports in <CIM classes>
 -c [MODEL...]             Combine multiple models

 modeler:
 -m MODELER <args...>      Use [modeler] with arguments:

""" + Modelers.help

  val incorrectArgs = "Incorrect parameters, see help (trigram -h)."

  def main(args: Array[String]) = {
    println("Triple Graph based Metadata storage - TriGraM")

    args.toList match {
      case Nil => Console.run
      case "-h" :: Nil => println(usage)
      case "-v" :: Nil => println(Version.get)
      case "-i" :: modelFiles => modelFiles.foreach(Engine.tdbloader)
      case "-q" :: queryFile :: Nil => Engine.doQuery(GetString.fromFile(queryFile))
      case "-u" :: updateFile :: Nil => Engine.doUpdate(GetString.fromFile(updateFile))
      case "-q" :: Nil => Engine.doQuery(GetString.fromConsole)
      case "-u" :: Nil => Engine.doUpdate(GetString.fromConsole)
      case "-s" :: cimXML :: Nil => {
        Schema.fromXML(cimXML).toModelGroup
        println("CIM Schema, Vocabulary updated.\n" + Schema.validate)
      }
      case "-s1" :: cimXML :: Nil => Schema.fromXML(cimXML).toModel
      case "-g" :: output :: Nil => Merger.gather(output, Modelers.tbox.toList)
      case "-g" :: output :: selected => Merger.gather(output, selected)
      case "-c" :: modelFiles =>
        if (modelFiles.size > 1) {
          Merger.combine(modelFiles)
          println(modelFiles.size + " models combined.")
        } else println("There's only one model out there.")
      case "-m" :: modeler :: margs => {
        println("invoking [%s] modeler with options [%s]"
          .format(modeler, margs.mkString(" ")))
        Modelers.run(modeler, margs.toArray)
      }
      case _ => println(incorrectArgs)
    }
  }
}