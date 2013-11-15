/**
 * plain text CSV modeler
 */
package modeler

import java.io.{ File, FileReader, FileOutputStream, OutputStreamWriter, BufferedWriter }
import scala.xml.Utility.escape
import util.{ Logging, Version, DateTime, URI, CSVReader, Strings }

/**
 * @author ShiZhan
 * plain text CSV translator for handling large document
 */
object CSVex extends Modeler with Logging {
  override val key = "csvex"

  override val usage =
    "<CSV> <index column> [<uri file>] => [triples]," +
      "\n\t\tplain text translation to support large document."

  def run(options: Array[String]) = {
    val defaultNS = URI.fromHost + "/CSV#"
    val defaultNames = List("ROW") ++ { (0 to 127) map { "COL%03d".format(_) } }
    val defaultURIs = defaultNames map (defaultNS + _)
    options.toList match {
      case data :: index :: Nil => translate(data, index.toInt, defaultURIs)
      case data :: index :: nameFile :: Nil => {
        val lines = Strings.fromFile(nameFile)
        val len = lines.length
        val uris = if (len < 128) lines ++ defaultURIs.drop(len) else lines

        logger.info("[{}] URIs used:", lines.length)
        lines foreach println
        logger.warn("If any of them are declared in other models such as CIM models,")
        logger.warn("they should be imported or combined if needed.")

        translate(data, index.toInt, uris)
      }
      case _ => { logger.error("parameter error: [{}]", options) }
    }
  }

  private def prefixT = (index: Int, ns: String) => s"""
    xmlns:n.$index="$ns""""

  private def headerT =
    (prefixes: String, base: String, version: String, dateTime: String, cUri: String) =>
      s"""<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:owl="http://www.w3.org/2002/07/owl#"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema#"$prefixes
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#">
  <owl:Ontology rdf:about="$base">
    <owl:versionInfo rdf:datatype="http://www.w3.org/2001/XMLSchema#string"
    >$version</owl:versionInfo>
    <dc:description rdf:datatype="http://www.w3.org/2001/XMLSchema#string"
    >TriGraM CSV model</dc:description>
    <dc:date rdf:datatype="http://www.w3.org/2001/XMLSchema#dateTime"
    >$dateTime</dc:date>
  </owl:Ontology>
  <owl:Class rdf:about="$cUri"/>"""

  private def dataTypePropertyT = (uri: String) => s"""
  <owl:DatatypeProperty rdf:about="$uri"/>"""

  private def hasPropertyT = (ns: String, pName: String, value: String) => s"""
    <$ns:$pName rdf:datatype="http://www.w3.org/2001/XMLSchema#normalizedString"
    >$value</$ns:$pName>"""

  private def individualT =
    (ns: String, cName: String, uri: String, hasProperties: String) => s"""
  <$ns:$cName rdf:about="$uri">$hasProperties
  </$ns:$cName>"""

  private val footerT = """
</rdf:RDF>"""

  def translate(data: String, index: Integer, uris: List[String]) = {
    val output = data + "-model.owl"
    val m = new BufferedWriter(
      new OutputStreamWriter(new FileOutputStream(output), "UTF-8"))

    val base = URI.fromHost

    val PrefixAndName = uris map { p =>
      val splitAtPosition = (p.lastIndexOf('#') max p.lastIndexOf('/')) + 1
      (p.substring(0, splitAtPosition), p.substring(splitAtPosition))
    }
    val nsList = PrefixAndName map (_._1) distinct
    def nsOf(uriIndex: Int) = "n." + nsList.indexOf(PrefixAndName(uriIndex)._1)
    def nameOf(uriIndex: Int) = PrefixAndName(uriIndex)._2

    val prefixes = (0 to nsList.length - 1) map { i => prefixT(i, nsList(i))} mkString

    val cURI = uris.head
    val pURI = uris.drop(1)
    val properties = pURI.map { dataTypePropertyT(_) }.mkString

    m.write(headerT(prefixes, base, Version.get, DateTime.get, cURI) + properties)

    val reader = new CSVReader(new File(data), ';')
    val entries = reader.iterator
    for (e <- entries) {
      val i = escape(e(index))
      val hasProperties = (0 to e.length - 1).map {
        c => hasPropertyT(nsOf(c + 1), nameOf(c + 1), escape(e(c)))
      }.mkString
      val individual = individualT(nsOf(0), nameOf(0), URI.fromString(i), hasProperties)
      m.write(individual)
    }

    m.write(footerT)
    m.close

    logger.info("generated individuals written to [{}]", output)
  }
}