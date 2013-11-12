/**
 * Modeler to translate CSV file into triple model
 */
package modeler

import java.io.{ File, FileReader, FileOutputStream }
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary.{ OWL, DC_11 => DC }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import util.{ Logging, Version, DateTime, URI, CSVReader, Text }

/**
 * @author ShiZhan
 * Translate CSV into triple model
 * A wide range support for various data sources that can be represented by CSV
 * CSV format:
 * delimiter: ';'
 * index column: key property, individual ID = base URI + key
 * column 0~127: all other properties as "COLxxx"
 * rows:         individuals of "ROW"
 * schema (optional) format:
 * row 0:   The name of concept that holds all the individuals (rows)
 * row 1~m: The name of properties that connect all the values (columns)
 */
object CSV extends Modeler with Logging {
  override val key = "csv"

  override val usage = "<CSV> <index column> [<names>] => [triples]"

  def run(options: Array[String]) = {
    val defaultNames = List("ROW") ++ { (0 to 127) map { "COL%03d".format(_) } }
    options.toList match {
      case data :: index :: Nil => translate(data, index.toInt, defaultNames)
      case data :: index :: nameFile :: Nil => {
        val lines = Text.readAllLines(nameFile)
        val len = lines.length
        val names = if (len < 128) lines ++ defaultNames.drop(len) else lines
        translate(data, index.toInt, names)
      }
      case _ => { logger.error("parameter error: [{}]", options) }
    }
  }

  def translate(data: String, index: Integer, names: List[String]) = {
    val base = URI.fromHost
    val ns = base + "/CSV#"
    val m = ModelFactory.createOntologyModel
    m.setNsPrefix(key, ns)
    m.createOntology(base)
      .addProperty(DC.date, DateTime.get, XSDdateTime)
      .addProperty(DC.description, "TriGraM CSV model", XSDstring)
      .addProperty(OWL.versionInfo, Version.get, XSDstring)

    val rowName = names.head
    val colName = names.drop(1)
    val Concept = m.createClass(ns + rowName)
    val Properties = colName map { n => m.createDatatypeProperty(ns + n) }

    val reader = new CSVReader(new File(data), ';')
    val entries = reader.iterator
    for (e <- entries) {
      val i = e(index)
      val uri = URI.fromString(i)
      val r = m.createIndividual(uri, Concept)
      (0 to e.length - 1) foreach {
        c => r.addProperty(Properties(c), e(c), XSDnormalizedString)
      }
    }

    val output = data + "-model.owl"
    m.write(new FileOutputStream(output), "RDF/XML-ABBREV")

    logger.info("[{}] triples generated in [{}]", m.size, output)
  }
}