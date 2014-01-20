/**
 * Modeler to translate directory
 */
package modeler

/**
 * @author ShiZhan
 * translate directory structure into semantic model with CIM vocabularies
 * use "--text" option to do pure text translation for HUGE directory structure
 */
object DirectoryModels {
  import java.io.File
  import xml.Utility.escape
  import com.hp.hpl.jena.rdf.model.Model
  import com.hp.hpl.jena.vocabulary.{ RDF, RDFS, OWL, OWL2, DC_11 => DC, DCTerms => DT }
  import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
  import helper.{ Logging, Version, DateTime }
  import common.URI
  import cim.{ Vocabulary => CIM }

  case class DirectoryTreeModel(base: String, prefix: String) {
    val modelName = "TriGraM Directory model"
    val dateTime = DateTime.get
    val version = Version.get
    val cimNs = CIM.NS
    val cimPrefix = CIM.NS_PREFIX

    def create(m: Model) = {
      m.setNsPrefix(prefix, base + "#")
      m.setNsPrefix(cimPrefix, cimNs)
      val ont = m.createResource(base, OWL.Ontology)
        .addProperty(DC.date, dateTime, XSDdateTime)
        .addProperty(DC.description, modelName, XSDstring)
        .addProperty(OWL.versionInfo, version, XSDstring)
      m
    }

    val header = {
      s"""<rdf:RDF
      xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
      xmlns:owl="http://www.w3.org/2002/07/owl#"
      xmlns:dc="http://purl.org/dc/elements/1.1/"
      xmlns:$cimPrefix="$cimNs"
      xmlns:$prefix="$base#">
    <owl:Ontology rdf:about="$base">
      <owl:versionInfo rdf:datatype="http://www.w3.org/2001/XMLSchema#string"
      >$version</owl:versionInfo>
      <dc:description rdf:datatype="http://www.w3.org/2001/XMLSchema#string"
      >$modelName</dc:description>
      <dc:date rdf:datatype="http://www.w3.org/2001/XMLSchema#dateTime"
      >$dateTime</dc:date>
    </owl:Ontology>"""
    }

    val footer = "\n</rdf:RDF>"
  }

  implicit class FileModel(f: File) {
    val name = f.getAbsolutePath
    val size = f.length.toString
    val lastMod = DateTime.get(f.lastModified)
    val canRead = f.canRead.toString
    val canWrite = f.canWrite.toString
    val canExecute = f.canExecute.toString
    val isDirectory = f.isDirectory
    val uri = URI.fromFile(f)

    def addTo(m: Model) = {
      val res = m.createResource(uri, OWL2.NamedIndividual)
        .addProperty(CIM.PROP("Name"), name, XSDnormalizedString)
        .addProperty(CIM.PROP("FileSize"), size, XSDunsignedLong)
        .addProperty(CIM.PROP("LastModified"), lastMod, XSDdateTime)
        .addProperty(CIM.PROP("Readable"), canRead, XSDboolean)
        .addProperty(CIM.PROP("Writeable"), canWrite, XSDboolean)
        .addProperty(CIM.PROP("Executable"), canExecute, XSDboolean)
      if (isDirectory) {
        res.addProperty(RDF.`type`, CIM.CLASS("CIM_Directory"))
          .addProperty(RDF.`type`, CIM.CLASS("CIM_DirectoryContainsFile"))
        f.listFiles foreach { sFile =>
          res.addProperty(CIM.PROP("PartComponent"), m.getResource(URI.fromFile(sFile)))
        }
        res.addProperty(CIM.PROP("GroupComponent"), res)
      } else {
        res.addProperty(RDF.`type`, CIM.CLASS("CIM_DataFile"))
      }
    }

    def toOWL = {
      val uriInXML = escape(uri)
      val nameInXML = escape(name)
      val cimClass = if (isDirectory) CIM.URI("CIM_Directory") else CIM.URI("CIM_DataFile")
      val cimDCF = CIM.URI("CIM_DirectoryContainsFile")
      val dcf = if (isDirectory) {
        val partComponent =
          ("" /: f.listFiles) { (r, p) =>
            r + "\n    <cim:PartComponent rdf:resource=\"%s\"/>"
              .format(escape(URI.fromFile(p)))
          }
        s"""
      <rdf:type rdf:resource="$cimDCF"/>
      <cim:GroupComponent rdf:resource="$uriInXML"/>$partComponent"""
      } else ""

      s"""
    <owl:NamedIndividual rdf:about="$uriInXML">
      <rdf:type rdf:resource="$cimClass"/>
      <cim:Name rdf:datatype="http://www.w3.org/2001/XMLSchema#normalizedString"
      >$nameInXML</cim:Name>
      <cim:FileSize rdf:datatype="http://www.w3.org/2001/XMLSchema#unsignedLong"
      >$size</cim:FileSize>
      <cim:LastModified rdf:datatype="http://www.w3.org/2001/XMLSchema#dateTime"
      >$lastMod</cim:LastModified>
      <cim:Readable rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean"
      >$canRead</cim:Readable>
      <cim:Writeable rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean"
      >$canWrite</cim:Writeable>
      <cim:Executable rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean"
      >$canExecute</cim:Executable>$dcf
    </owl:NamedIndividual>"""
    }
  }

  implicit class ModelEx(m: Model) {
    def set(base: String, prefix: String) = DirectoryTreeModel(base, prefix).create(m)
    def +=(fileModel: FileModel) = fileModel.addTo(m)
  }
}

object Directory extends Modeler with helper.Logging {
  import java.io.File
  import com.hp.hpl.jena.rdf.model.ModelFactory
  import DirectoryModels._
  import common.FileEx.FileOps
  import common.ModelEx.ModelOps
  import common.URI

  val key = "dir"

  val usage = "[input] [output.owl] <--text> => output.owl"

  val tbox = Seq("CIM_Directory", "CIM_DataFile", "CIM_DirectoryContainsFile")

  def run(options: List[String]) = {
    options match {
      case input :: output :: Nil => translate(input, output)
      case input :: output :: "--text" :: Nil => translateEx(input, output)
      case _ => logger.error("incorrect options: [{}]", options)
    }
  }

  private def translate(input: String, output: String) = {
    logger.info("creating model ...")

    val f = new File(input)
    val m = ModelFactory.createDefaultModel.set(URI.fromHost, key)
    m += f

    logger.info("reading directory ...")

    val files = f.flatten.zipWithIndex
    val total = files.size
    val delta = if (total < 100) 1 else total / 100

    logger.info("[{}] files found", total)

    for ((file, i) <- files) {
      m += file
      if (i % delta == 0) print("translating [%2d%%]\r".format(i * 100 / total))
    }
    println("translating [100%]")

    m.store(output)
  }

  private def translateEx(input: String, output: String) = {
    logger.info("creating model ...")

    val f = new File(input)
    val model = DirectoryTreeModel(URI.fromHost, key)
    val modelFile = new File(output) getWriter "UTF-8"
    modelFile write (model.header + f.toOWL)

    logger.info("reading directory ...")

    val files = f.flatten.zipWithIndex
    val total = files.length
    val delta = if (total < 100) 1 else total / 100

    logger.info("[{}] files found", total)

    for ((file, i) <- files) {
      modelFile write file.toOWL
      if (i % delta == 0) print("translating [%2d%%]\r".format(i * 100 / total))
    }
    println("translating [100%]")

    modelFile write model.footer
    modelFile.close

    logger.info("[{}] individuals generated in [{}]", total, output)
  }
}