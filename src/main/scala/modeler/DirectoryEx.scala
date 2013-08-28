/**
 * Modeler to translate directory
 */
package modeler

import scala.xml.Utility.escape
import java.io.{ File, FileOutputStream, OutputStreamWriter, BufferedWriter }
import scalax.file.Path
import util.{ Logging, Version, DateTime, Hash }

/**
 * @author ShiZhan
 * translate HUGE directory structure into TriGraM model
 * using string interpolation
 */
object DirectoryEx extends Modeler with Logging {

  override val key = "direx"

  override val usage = "Translate *HUGE* directory structure"

  def tBox = Directory.tBox // obsolete

  def aBox(input: String, output: String) = {
    val p = Path(new File(input))

    if (p.isDirectory) {
      logger.info("creating model for *HUGE* directory [{}]", p.path)

      def headerT =
        (base: String, version: String, dateTime: String) =>
          s"""<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:owl="http://www.w3.org/2002/07/owl#"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:prop="https://sites.google.com/site/ontology2013/CIM_Properties.owl#"
    xmlns:dir="https://sites.google.com/site/ontology2013/CIM_Directory.owl#"
    xmlns:df="https://sites.google.com/site/ontology2013/CIM_DataFile.owl#"
    xmlns:dcf="https://sites.google.com/site/ontology2013/CIM_DirectoryContainsFile.owl#">
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
        (base: String, nodeId: String, cimClass: String,
          name: String, size: Long, lastModified: String,
          canRead: Boolean, canWrite: Boolean, canExecute: Boolean) => s"""
  <owl:NamedIndividual rdf:about="$base#$nodeId">
    <rdf:type rdf:resource="$cimClass"/>
    <prop:Name rdf:datatype="http://www.w3.org/2001/XMLSchema#normalizedString"
    >$name</prop:Name>
    <prop:FileSize rdf:datatype="http://www.w3.org/2001/XMLSchema#unsignedLong"
    >$size</prop:FileSize>
    <prop:LastModified rdf:datatype="http://www.w3.org/2001/XMLSchema#dateTime"
    >$lastModified</prop:LastModified>
    <prop:Readable rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean"
    >$canRead</prop:Readable>
    <prop:Writeable rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean"
    >$canWrite</prop:Writeable>
    <prop:Executable rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean"
    >$canExecute</prop:Executable>
  </owl:NamedIndividual>
"""

      def partComponentT = (base: String, subNodeId: String) => s"""
    <prop:PartComponent rdf:resource="$base#$subNodeId"/>"""

      def directoryContainsFileT =
        (base: String, dcfId: String, partComponent: String, dirId: String) => s"""
  <dcf:CIM_DirectoryContainsFile rdf:about="$base#$dcfId">
    $partComponent
    <prop:GroupComponent rdf:resource="$base#$dirId"/>
    <rdf:type rdf:resource="http://www.w3.org/2002/07/owl#NamedIndividual"/>
  </dcf:CIM_DirectoryContainsFile>"""

      val footerT = "</rdf:RDF>"

      def nodeT(uri: String, node: Path) = {
        val nodeId = Hash.getMD5(node.path)

        val isDirectory = node.isDirectory

        val cimClass =
          if (isDirectory) DIR.CLASS("CIM_Directory")
          else DIR.CLASS("CIM_DataFile")
        val name = escape(node.name)
        val size = if (node.size.nonEmpty) node.size.get else 0
        val dateTime = DateTime.get(node.lastModified)

        val logicalFile = logicalFileT(uri, nodeId, cimClass.toString,
          name, size, dateTime, node.canRead, node.canWrite, node.canExecute)

        val directoryConainsFile = if (isDirectory) {
          val iSub = node * "*"
          val partComponent =
            iSub.map(s => partComponentT(uri, Hash.getMD5(s.path))).mkString
          directoryContainsFileT(uri, nodeId + "_dcf", partComponent, nodeId)
        } else ""
        logicalFile + directoryConainsFile
      }

      val m = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(output), "UTF-8"))

      val base = p.toURI.toString

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

      logger.info("[{}] individuals generated", total)
    } else {
      logger.info("[{}] is not a directory", p.name)
    }
  }

}