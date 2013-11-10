/**
 * plain text CSV modeler
 */
package modeler

import java.io.{ FileReader, FileOutputStream, OutputStreamWriter, BufferedWriter }
import scala.xml.Utility.escape
import au.com.bytecode.opencsv.CSVReader
import util.{ Logging, Version, DateTime, URI }

/**
 * @author ShiZhan
 * plain text CSV translator for handling large document
 */
object CSVex extends Modeler with Logging {
  override val key = "csvex"

  override val usage =
    "<CSV> => [triples],\n\t\tplain text translation to support large document."

  def run(options: Array[String]) = {
    options.toList match {
      case data :: tail => translate(data)
      case _ => { logger.error("parameter error: [{}]", options) }
    }
  }

  private def headerT = (ns: String, base: String, version: String, dateTime: String) =>
    s"""<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:owl="http://www.w3.org/2002/07/owl#"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
    xmlns:csv="$ns"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#">
  <owl:Ontology rdf:about="$base">
    <owl:versionInfo rdf:datatype="http://www.w3.org/2001/XMLSchema#string"
    >$version</owl:versionInfo>
    <dc:description rdf:datatype="http://www.w3.org/2001/XMLSchema#string"
    >TriGraM CSV model</dc:description>
    <dc:date rdf:datatype="http://www.w3.org/2001/XMLSchema#dateTime"
    >$dateTime</dc:date>
  </owl:Ontology>"""

  private def dataTypePropertyT = (uri: String) =>
    s"""
  <owl:DatatypeProperty rdf:about="$uri"/>"""

  private def hasPropertyT = (name: String, value: String) =>
    s"""
    <csv:$name rdf:datatype="http://www.w3.org/2001/XMLSchema#normalizedString"
    >$value</csv:$name>"""

  private def individualT = (uri: String, hasProperties: String) =>
    s"""
  <csv:ROW rdf:about="$uri">$hasProperties
  </csv:ROW>"""

  private val footerT = """
</rdf:RDF>"""

  def translate(data: String) = {
    val output = data + "-model.owl"
    val m = new BufferedWriter(
      new OutputStreamWriter(new FileOutputStream(output), "UTF-8"))

    val base = URI.fromHost
    val ns = base + "/CSV#"
    def pName(i: Int) = "COL%03d".format(i)
    val properties = (0 to 127).map { i => dataTypePropertyT(ns + pName(i)) }.mkString
    m.write(headerT(ns, base, Version.get, DateTime.get) + properties)

    val reader = new CSVReader(new FileReader(data), '*')
    val entries = Iterator.continually { reader.readNext }.takeWhile(_ != null)
    for (e <- entries) {
      val key = e(0)
      val hasProperties = (0 to e.length - 1).map {
        i => hasPropertyT(pName(i), escape(e(i)))
      }.mkString
      val individual = individualT(URI.fromString(key), hasProperties)
      m.write(individual)
    }
    reader.close

    m.write(footerT)
    m.close

    logger.info("generated individuals written to [{}]", output)
  }
}