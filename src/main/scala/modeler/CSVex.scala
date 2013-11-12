/**
 * plain text CSV modeler
 */
package modeler

import java.io.{ File, FileReader, FileOutputStream, OutputStreamWriter, BufferedWriter }
import scala.xml.Utility.escape
import util.{ Logging, Version, DateTime, URI, CSVReader }

/**
 * @author ShiZhan
 * plain text CSV translator for handling large document
 */
object CSVex extends Modeler with Logging {
  override val key = "csvex"

  override val usage =
    "<CSV> <index column> [<names>] => [triples]," +
      "\n\t\tplain text translation to support large document."

  def run(options: Array[String]) = {
    val defaultNames = List("ROW") ++ { (0 to 127) map { "COL%03d".format(_) } }
    options.toList match {
      case data :: index :: Nil => translate(data, index.toInt, defaultNames)
      case data :: index :: nameFile :: Nil => {
        val f = io.Source.fromFile(new File(nameFile))
        val lines = f.getLines.toList
        val len = lines.length
        val names = if (len < 128) lines ++ defaultNames.drop(len) else lines
        translate(data, index.toInt, names)
      }
      case _ => { logger.error("parameter error: [{}]", options) }
    }
  }

  private def headerT =
    (ns: String, base: String, version: String, dateTime: String, uri: String) =>
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
  </owl:Ontology>
  <owl:Class rdf:about="$uri"/>"""

  private def dataTypePropertyT = (uri: String) => s"""
  <owl:DatatypeProperty rdf:about="$uri"/>"""

  private def hasPropertyT = (name: String, value: String) => s"""
    <csv:$name rdf:datatype="http://www.w3.org/2001/XMLSchema#normalizedString"
    >$value</csv:$name>"""

  private def individualT = (cName: String, uri: String, hasProperties: String) => s"""
  <csv:$cName rdf:about="$uri">$hasProperties
  </csv:$cName>"""

  private val footerT = """
</rdf:RDF>"""

  def translate(data: String, index: Integer, names: List[String]) = {
    val output = data + "-model.owl"
    val m = new BufferedWriter(
      new OutputStreamWriter(new FileOutputStream(output), "UTF-8"))

    val base = URI.fromHost
    val ns = base + "/CSV#"
    val rowName = names.head
    val colName = names.drop(1)
    val concept = ns + rowName
    val properties = colName.map { n => dataTypePropertyT(ns + n) }.mkString
    m.write(headerT(ns, base, Version.get, DateTime.get, concept) + properties)

    val reader = new CSVReader(new File(data), ';')
    val entries = reader.iterator
    for (e <- entries) {
      val i = escape(e(index))
      val hasProperties = (0 to e.length - 1).map {
        c => hasPropertyT(colName(c), escape(e(c)))
      }.mkString
      val individual = individualT(rowName, URI.fromString(i), hasProperties)
      m.write(individual)
    }

    m.write(footerT)
    m.close

    logger.info("generated individuals written to [{}]", output)
  }
}