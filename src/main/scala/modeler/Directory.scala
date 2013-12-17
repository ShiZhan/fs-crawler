/**
 * Modeler to translate directory
 */
package modeler

import java.io.{ File, FileOutputStream, BufferedWriter, OutputStreamWriter }
import xml.Utility.escape
import com.hp.hpl.jena.rdf.model.{ ModelFactory, Model }
import com.hp.hpl.jena.vocabulary.{ RDF, RDFS, OWL, OWL2, DC_11 => DC, DCTerms => DT }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import util.{ Logging, Version, DateTime, URI }
import modeler.{ CimVocabulary => CIM }

/**
 * @author ShiZhan
 * translate directory structure into semantic model with CIM vocabularies
 * use "--text" option to do pure text translation for HUGE directory structure
 */
case class DirectoryTreeModel(base: String, prefix: String) {
  val modelName = "TriGraM Directory model"
  val dateTime = DateTime.get
  val version = Version.get
  val cimNs = CIM.NS
  val cimPrefix = CimSchema.key
  val imports = Seq("CIM_Directory", "CIM_DataFile", "CIM_DirectoryContainsFile")

  def create = {
    val m = ModelFactory.createDefaultModel
    m.setNsPrefix(prefix, base + "#")
    m.setNsPrefix(cimPrefix, cimNs)
    val ont = m.createResource(base, OWL.Ontology)
      .addProperty(DC.date, dateTime, XSDdateTime)
      .addProperty(DC.description, modelName, XSDstring)
      .addProperty(OWL.versionInfo, version, XSDstring)
    imports foreach { i => ont.addProperty(OWL.imports, CIM.IMPORT(i)) }
    m
  }

  val header = {
    val importStatments =
      ("" /: imports) { (r, i) =>
        r + "\n    <owl:imports rdf:resource=\"%s\"/>".format(CIM.PURL(i))
      }
    s"""<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:owl="http://www.w3.org/2002/07/owl#"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:$cimPrefix="$cimNs"
    xmlns:$prefix="$base#">
  <owl:Ontology rdf:about="$base">$importStatments
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

case class FileModel(f: File) {
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

  override def toString = {
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

object Directory extends Modeler with Logging {

  override val key = "dir"

  override val usage = "<directory> <output> [<--text>] => [triples]"

  def listAllFiles(f: File): Array[File] = {
    val list = f.listFiles
    if (list == null) Array[File]() else list ++ list.filter(_.isDirectory).flatMap(listAllFiles)
  }

  private def translate(f: File, output: String) = {
    logger.info("creating model ...")

    val m = DirectoryTreeModel(URI.fromHost, key).create
    FileModel(f).addTo(m)

    logger.info("reading directory ...")

    val files = listAllFiles(f).zipWithIndex
    val total = files.size
    val delta = if (total < 100) 1 else total / 100

    logger.info("[{}] files found", total)

    files foreach {
      case (file, i) =>
        FileModel(file).addTo(m)
        if (i % delta == 0) print("translating [%2d%%]\r".format(i * 100 / total))
    }
    println("translating [100%]")

    m.write(new FileOutputStream(output), "RDF/XML-ABBREV")

    logger.info("[{}] triples generated in [{}]", m.size, output)
  }

  private def translateEx(f: File, output: String) = {
    logger.info("creating model ...")

    val m = new BufferedWriter(
      new OutputStreamWriter(new FileOutputStream(output), "UTF-8"))
    val owl = DirectoryTreeModel(URI.fromHost, key)
    m.write(owl.header + FileModel(f))

    logger.info("reading directory ...")

    val files = listAllFiles(f).zipWithIndex
    val total = files.size
    val delta = if (total < 100) 1 else total / 100

    logger.info("[{}] files found", total)

    files foreach {
      case (file, i) =>
        m.write(FileModel(file).toString)
        if (i % delta == 0) print("translating [%2d%%]\r".format(i * 100 / total))
    }
    println("translating [100%]")

    m.write(owl.footer)
    m.close

    logger.info("[{}] individuals generated in [{}]", total, output)
  }

  def run(options: Array[String]) = {
    options.toList match {
      case file :: output :: Nil =>
        translate(new File(file), output)
      case file :: output :: "--text" :: Nil =>
        translateEx(new File(file), output)
      case _ => logger.error("incorrect options: [{}]", options)
    }
  }
}