/**
 * @author ShiZhan
 * @year 2013
 * @name TriGraM Project
 */
object Trigram {
  import tdb.tdbloader.{ main => loader }
  import tdb.tdbquery.{ main => query }
  import tdb.tdbupdate.{ main => update }
  import console.Console
  import modeler.{ Modelers, CimVocabulary, Merger }
  import util.{ Config, Version }

  val usage = """
usage: Trigram
 -h,--help                print this message
 -v,--version             show program version

 console:
 <no argument>            enter console

 database:
 -i,--import MODEL        import model
 -q,--query SPARQL        http://www.w3.org/TR/sparql11-query/
 -u,--update SPARQL       http://www.w3.org/TR/sparql11-update/

 model cookers:
 -c,--combine <MODEL...>         Combine multiple models
 -V,--vocabulary CIM_Schema_XML  Update CIM vocabulary for use in modelers
                                 [CIM Schema XML] can be downloaded from DMTF:
                                 http://dmtf.org/standards/cim
 -g,--gather MODEL               Gather CIM imports into given model
                                 Based on the <OWL.imports> in [MODEL].

 modeler:
 -m,--modeler MODELER <args...>  Use [modeler] with arguments (below)
                                 Each [modeler] has its own arguments.

""" + Modelers.help

  val incorrectArgs = "Incorrect parameters, see help (Translator -h, --help)."

  type OptionMap = Map[Symbol, Any]

  def parseOption(list: List[String]): OptionMap = {
    list match {
      case "-h" :: tail => Map('help -> true)
      case "--help" :: tail => Map('help -> true)
      case "-v" :: tail => Map('version -> true)
      case "--version" :: tail => Map('version -> true)
      case "-i" :: modelList => Map('model -> modelList)
      case "--import" :: modelList => Map('model -> modelList)
      case "-q" :: q :: tail => Map('query -> q)
      case "--query" :: q :: tail => Map('query -> q)
      case "-u" :: u :: tail => Map('update -> u)
      case "--update" :: u :: tail => Map('update -> u)
      case "-c" :: models => Map('combine -> models)
      case "--combine" :: models => Map('combine -> models)
      case "-V" :: cimSchema :: tail => Map('schema -> cimSchema)
      case "--vocabulary" :: cimSchema :: tail => Map('schema -> cimSchema)
      case "-g" :: baseModel :: tail => Map('gather -> baseModel)
      case "--gather" :: baseModel :: tail => Map('gather -> baseModel)
      case "-m" :: modeler :: margs =>
        Map('modeler -> modeler, 'margs -> margs)
      case "--modeler" :: modeler :: margs =>
        Map('modeler -> modeler, 'margs -> margs)
      case _ => Map(): OptionMap
    }
  }

  def main(args: Array[String]) = {
    println("Triple Graph based Metadata storage - TriGraM")

    if (args.length == 0) Console.run
    else {
      val tgmData = Config.TGMDATA
      val cimData = Config.CIMDATA
      val options = parseOption(args.toList)

      if (options.isEmpty) println(incorrectArgs)
      else if (options.contains('help)) println(usage)
      else if (options.contains('version)) println(Version.get)
      else if (options.contains('model)) {
        val modelList = options('model).asInstanceOf[List[String]]
        modelList.foreach(loader(s"--loc=$tgmData", _))
      } else if (options.contains('query)) {
        val queryFile = options('query).toString
        query(s"--loc=$tgmData", "--query=" + queryFile)
      } else if (options.contains('update)) {
        val updateFile = options('update).toString
        update(s"--loc=$tgmData", "--update=" + updateFile)
      } else if (options.contains('combine)) {
        val modelFiles = options('combine).asInstanceOf[List[String]]
        if (modelFiles.size > 1) {
          Merger.combine(modelFiles)
          println("[%d] models combined.".format(modelFiles.size))
        } else println("There's only one model out there.")
      } else if (options.contains('schema)) {
        val cimSchema = options('schema).toString
        CimVocabulary.generator(cimSchema)
        println("CIM Vocabulary in [%s] are updated.".format(cimData))
      } else if (options.contains('gather)) {
        val baseModel = options('gather).toString
        Merger.gather(baseModel)
        println("All imported CIM classes of [%s] gathered.".format(baseModel))
      } else if (options.contains('modeler)) {
        val m = options('modeler).toString
        val o = options('margs).asInstanceOf[List[String]].toArray
        println("invoking [%s] modeler with options [%s]".format(m, o.mkString(" ")))
        Modelers.run(m, o)
      }
    }
  }
}