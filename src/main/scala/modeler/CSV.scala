/**
 * Modeler to translate CSV file into triple model
 */
package modeler

import java.io.{ File, FileReader, FileOutputStream }
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.ontology.{ OntModel, OntClass, DatatypeProperty }
import com.hp.hpl.jena.vocabulary.{ OWL, DC_11 => DC }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import CimVocabulary.{ isCimURI, URI2PURL }
import util.{ Logging, Version, DateTime, URI, CSVReader, Strings }

/**
 * @author ShiZhan
 * Translate CSV into triple model
 * A wide range support for various data sources that can be represented by CSV
 * CSV format:
 * delimiter: ';'
 * index column: key property, individual ID = base URI + key
 * column 0~127: all other properties as "COLxxx"
 * rows:         individuals of "ROW"
 * uri file (optional) format:
 * row 0:   The name of concept that holds all the individuals (rows)
 * row 1~m: The name of properties that connect all the values (columns)
 * NOTE:
 * the concept should be imported/combined for the resulting model to be
 * fully functional
 */
case class CsvModel(m: OntModel, c: OntClass, p: List[DatatypeProperty])

case class CsvHeaderModel(base: String, uris: List[String]) {
  val cURI = uris.head
  val pURI = uris.tail
  def getModel = {
    val m = ModelFactory.createOntologyModel
    val o = m.createOntology(base)
      .addProperty(DC.date, DateTime.get, XSDdateTime)
      .addProperty(DC.description, "TriGraM CSV model", XSDstring)
      .addProperty(OWL.versionInfo, Version.get, XSDstring)
    if (isCimURI(cURI))
      o.addProperty(OWL.imports, m.createResource(URI2PURL(cURI)))
    val c = m.createClass(cURI)
    val p = pURI map { m.createDatatypeProperty(_) }
    CsvModel(m, c, p)
  }
}

case class CsvEntryModel(e: Array[String], index: Integer) {
  val key = e(index)
  val uri = URI.fromString(key)
  def addTo(cm: CsvModel) = {
    val (m, c, p) = (cm.m, cm.c, cm.p)
    val r = m.createIndividual(uri, c)
    e.zipWithIndex.
      foreach { case (value, col) => r.addProperty(p(col), value, XSDnormalizedString) }
  }
}

object CSV extends Modeler with Logging {
  override val key = "csv"

  override val usage = "<CSV> <index column> [<uri file>] => [triples]"

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

        translate(data, index.toInt, uris)
      }
      case _ => { logger.error("parameter error: [{}]", options) }
    }
  }

  private def translate(data: String, index: Integer, uris: List[String]) = {
    val cm = CsvHeaderModel(URI.fromHost, uris).getModel
    val reader = new CSVReader(new File(data), ';')
    reader.iterator.foreach { CsvEntryModel(_, index).addTo(cm) }

    val output = data + "-model.owl"
    cm.m.write(new FileOutputStream(output), "RDF/XML-ABBREV")

    logger.info("[{}] triples generated in [{}]", cm.m.size, output)
  }
}