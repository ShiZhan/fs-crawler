/**
 * Modeler to translate directory
 */
package modeler

import scala.xml.Utility.escape
import java.io.{ File, FileOutputStream, OutputStreamWriter, BufferedWriter }
import scalax.file.Path
import util.{ Logging, Version, DateTime, URI }

/**
 * @author ShiZhan
 * translate HUGE directory structure into TriGraM model
 * using string interpolation
 */
object DirectoryEx extends Modeler with Logging {

  override val key = "direx"

  override val usage = "<directory> [<output>] => [triples],\n\t\tplain text translation to support massive items."

  def run(options: Array[String]) = {
    val input = options(0)
    val p = Path(new File(input))

    if (p.isDirectory) {
      logger.info("creating model for *HUGE* directory [{}]", p.toAbsolute.path)

      val base = URI.fromHost

      def headerT =
        (base: String, version: String, dateTime: String) =>
          s"""<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:owl="http://www.w3.org/2002/07/owl#"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:cim="https://sites.google.com/site/ontology2013/"
    xmlns:dir="$base#">
  <owl:Ontology rdf:about="$base">
    <owl:imports rdf:resource="https://sites.google.com/site/ontology2013/CIM_DirectoryContainsFile.owl"/>
    <owl:imports rdf:resource="https://sites.google.com/site/ontology2013/CIM_DataFile.owl"/>
    <owl:imports rdf:resource="https://sites.google.com/site/ontology2013/CIM_Directory.owl"/>
    <owl:versionInfo rdf:datatype="http://www.w3.org/2001/XMLSchema#string"
    >$version</owl:versionInfo>
    <dc:description rdf:datatype="http://www.w3.org/2001/XMLSchema#string"
    >TriGraM directory model</dc:description>
    <dc:date rdf:datatype="http://www.w3.org/2001/XMLSchema#dateTime"
    >$dateTime</dc:date>
  </owl:Ontology>"""

      def logicalFileT =
        (pathURI: String, cimClass: String,
          name: String, size: Long, lastModified: String,
          canRead: Boolean, canWrite: Boolean, canExecute: Boolean) => s"""
  <owl:NamedIndividual rdf:about="$pathURI">
    <rdf:type rdf:resource="$cimClass"/>
    <cim:Name rdf:datatype="http://www.w3.org/2001/XMLSchema#normalizedString"
    >$name</cim:Name>
    <cim:FileSize rdf:datatype="http://www.w3.org/2001/XMLSchema#unsignedLong"
    >$size</cim:FileSize>
    <cim:LastModified rdf:datatype="http://www.w3.org/2001/XMLSchema#dateTime"
    >$lastModified</cim:LastModified>
    <cim:Readable rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean"
    >$canRead</cim:Readable>
    <cim:Writeable rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean"
    >$canWrite</cim:Writeable>
    <cim:Executable rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean"
    >$canExecute</cim:Executable>
  </owl:NamedIndividual>
"""

      def partComponentT = (pathURI: String) => s"""
    <cim:PartComponent rdf:resource="$pathURI"/>"""

      def directoryContainsFileT =
        (pathURI: String, partComponent: String) => s"""
  <cim:CIM_DirectoryContainsFile rdf:about="$pathURI.dcf">
    $partComponent
    <cim:GroupComponent rdf:resource="$pathURI"/>
    <rdf:type rdf:resource="http://www.w3.org/2002/07/owl#NamedIndividual"/>
  </cim:CIM_DirectoryContainsFile>"""

      val footerT = "</rdf:RDF>"

      def nodeT(uri: String, node: Path) = {
        val pathURI = URI.fromPath(node)

        val isDirectory = node.isDirectory

        val cimClass =
          if (isDirectory)
            "https://sites.google.com/site/ontology2013/CIM_Directory"
          else
            "https://sites.google.com/site/ontology2013/CIM_DataFile"
        val name = escape(node.toAbsolute.path)
        val size = if (node.size.nonEmpty) node.size.get else 0
        val dateTime = DateTime.get(node.lastModified)

        val logicalFile = logicalFileT(pathURI, cimClass,
          name, size, dateTime, node.canRead, node.canWrite, node.canExecute)

        val directoryConainsFile = if (isDirectory) {
          val subNodeList = node * "*"
          val partComponent =
            subNodeList.map(s => partComponentT(URI.fromPath(s))).mkString
          directoryContainsFileT(pathURI, partComponent)
        } else ""
        logicalFile + directoryConainsFile
      }

      val output = if (options.length > 1) options(1) else input + "-model.owl"
      val m = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(output), "UTF-8"))

      m.write(headerT(base, Version.get, DateTime.get) + nodeT(base, p))

      logger.info("reading directory ...")

      val ps = p ** "*"

      val total = ps.size
      val delta = if (total < 100) 1 else total / 100
      var progress = 0

      logger.info("[{}] objects", total)

      for (i <- ps) {
        m.write(nodeT(base, i))

        progress += 1
        if (progress % delta == 0)
          print("translating [%2d%%]\r".format(progress * 100 / total))
      }
      println("translating [100%]")

      m.write(footerT)

      m.close

      logger.info("[{}] individuals generated in [{}]", total, output)
    } else {
      logger.info("[{}] is not a directory", p.name)
    }
  }

}