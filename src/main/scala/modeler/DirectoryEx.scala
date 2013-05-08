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

  def tBox = Directory.tBox

  def aBox(input: String, output: String) = {
    val p = Path(new File(input))

    if (p.isDirectory) {
      logger.info("creating model for *HUGE* directory [%s]".format(p.path))

      def headerT =
        (TBoxBase: String, base: String, version: String, dateTime: String) =>
          s"""<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:owl="http://www.w3.org/2002/07/owl#"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:dir="$TBoxBase#">
  <owl:Ontology rdf:about="$base">
    <owl:imports rdf:resource="$TBoxBase"/>
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
        (base: String, nodeId: String, TBoxBase: String, contains: String,
          name: String, size: Long, lastModified: String, isDirectory: Boolean,
          canRead: Boolean, canWrite: Boolean, canExecute: Boolean) => s"""
  <owl:NamedIndividual rdf:about="$base#$nodeId">
    <rdf:type rdf:resource="$TBoxBase#Object"/>
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
    <dir:isDirectory rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean"
    >$isDirectory</dir:isDirectory>
  $contains
  </owl:NamedIndividual>
"""

      val footerT = "</rdf:RDF>"

      val m = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(output), "UTF-8"))

      val base = p.toURI.toString
      val header = headerT(DIR.base, base, Version.get, DateTime.get)

      m.write(header)

      logger.info("reading directory ...")

      val ps = p ** "*"

      val total = ps.size
      val delta = if (total < 100) 1 else total / 100
      var progress = 0

      logger.info("[%d] objects".format(total))

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

        val individual = individualT(base, nodeId, DIR.base, contains, name,
          size, dateTime, isDirectory, i.canRead, i.canWrite, i.canExecute)

        m.write(individual)

        progress += 1
        if (progress % delta == 0)
          print("translating [%2d%%]\r".format(progress * 100 / total))
      }
      println("translating [100%]")

      m.write(footerT)

      m.close

      logger.info("[%d] individuals generated".format(total))
    } else {
      logger.info("[%s] is not a directory".format(p.name))
    }
  }

}