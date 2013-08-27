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
    xmlns:prop="https://sites.google.com/site/ontology2013/CIM_Properties.owl#">
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

      def containT = (base: String, subNodeId: String) => s"""
    <dir:contain rdf:resource="$base#$subNodeId"/>"""

      def individualT =
        (base: String, nodeId: String, cim_class: String,
          name: String, size: Long, lastModified: String,
          canRead: Boolean, canWrite: Boolean, canExecute: Boolean) => s"""
  <owl:NamedIndividual rdf:about="$base#$nodeId">
    <rdf:type rdf:resource="$cim_class"/>
    <dir:name rdf:datatype="http://www.w3.org/2001/XMLSchema#normalizedString"
    >$name</dir:name>
    <dir:size rdf:datatype="http://www.w3.org/2001/XMLSchema#unsignedLong"
    >$size</dir:size>
    <dir:lastModified rdf:datatype="http://www.w3.org/2001/XMLSchema#dateTime"
    >$lastModified</dir:lastModified>
    <dir:canRead rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean"
    >$canRead</dir:canRead>
    <dir:canWrite rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean"
    >$canWrite</dir:canWrite>
    <dir:canExecute rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean"
    >$canExecute</dir:canExecute>
  </owl:NamedIndividual>
"""

      val footerT = "</rdf:RDF>"

      val m = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(output), "UTF-8"))

      val base = p.toURI.toString
      val header = headerT(base, Version.get, DateTime.get)

      m.write(header)

      logger.info("reading directory ...")

      val ps = p ** "*"

      val total = ps.size
      val delta = if (total < 100) 1 else total / 100
      var progress = 0

      logger.info("[{}] objects", total)

      for (i <- ps) {
        val nodeId = Hash.getMD5(i.path)

        val isDirectory = i.isDirectory
        val contains = if (isDirectory) {
          val iSub = i * "*"
          iSub.map(s => containT(base, Hash.getMD5(s.path))).mkString
        } else ""

        val name = escape(i.name)
        val size = if (i.size.nonEmpty) i.size.get else 0
        val dateTime = DateTime.get(i.lastModified)

        val individual = individualT(base, nodeId, DIR.CLASS("CIM_DataFile").toString,
          name, size, dateTime, i.canRead, i.canWrite, i.canExecute)

        m.write(individual)

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