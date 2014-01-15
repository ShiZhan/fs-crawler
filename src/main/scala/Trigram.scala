/**
 * @author ShiZhan
 * @year 2012~2014
 * @name TriGraM Project
 */
object Trigram {
  import console.{ Console, Store, TDBWrapper }
  import modeler.{ Modelers, Merger }
  import cim.Schema
  import helper.Version
  import helper.Config.TGMDATA

  val usage = """usage: Trigram
 -h             print this message
 -v             show program version
 <no argument>  enter console

 database:
 -i MODEL       import model
 -q SPARQL      http://www.w3.org/TR/sparql11-query/
 -u SPARQL      http://www.w3.org/TR/sparql11-update/

 model operations:
 -c <MODEL...>         Combine multiple models.
 -s CIM_Schema_XML     Update CIM schema as TBox for modelers
                       [CIM Schema XML]: http://dmtf.org/standards/cim
 -g MODEL              Gather CIM imports into given model
                       Based on the <OWL.imports> in [MODEL].

 modeler:
 -m MODELER <args...>  Use [modeler] with arguments:

""" + Modelers.help
  val incorrectArgs = "Incorrect parameters, see help (trigram -h)."

  def main(args: Array[String]) = {
    println("Triple Graph based Metadata storage - TriGraM")

    args.toList match {
      case Nil => Console.run
      case "-h" :: tail => println(usage)
      case "-v" :: tail => println(Version.get)
      case "-i" :: modelList => modelList.foreach(TDBWrapper.loader)
      case "-q" :: queryFile :: tail => {
        val sparql = Console.fileInput(queryFile)
        Store(TGMDATA).doQuery(sparql)
      }
      case "-u" :: updateFile :: tail => {
        val sparql = Console.fileInput(updateFile)
        Store(TGMDATA).doUpdate(sparql)
      }
      case "-c" :: modelFiles =>
        if (modelFiles.size > 1) {
          Merger.combine(modelFiles)
          println("[%d] models combined.".format(modelFiles.size))
        } else println("There's only one model out there.")
      case "-s" :: cimXML :: tail => {
        val cimxml = Schema.fromXML(cimXML)
        cimxml.saveVocabulary
        cimxml.toModelGroup
        println("CIM Schema & Vocabulary updated.")
      }
      case "-s1" :: cimXML :: tail => Schema.fromXML(cimXML).toModel
      case "-g" :: baseModel :: tail => {
        Merger.gather(baseModel)
        println("All imported CIM classes of [%s] gathered.".format(baseModel))
      }
      case "-m" :: modeler :: margs => {
        println("invoking [%s] modeler with options [%s]"
          .format(modeler, margs.mkString(" ")))
        Modelers.run(modeler, margs.toArray)
      }
      case _ => println(incorrectArgs)
    }
  }
}