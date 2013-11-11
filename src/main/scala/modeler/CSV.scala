/**
 * Modeler to translate CSV file into triple model
 */
package modeler

import java.io.{ File, FileReader, FileOutputStream }
import au.com.bytecode.opencsv.CSVReader
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary.{ OWL, DC_11 => DC }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import util.{ Logging, Version, DateTime, URI }

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

  override val usage = "<CSV> <index column> [<schema>] => [triples]"

  def run(options: Array[String]) = {
    val defaultSchema = List("ROW") ++ { (0 to 127) map { "COL%03d".format(_) } }
    options.toList match {
      case data :: index :: Nil => translate(data, index.toInt, defaultSchema)
      case data :: index :: schemaFile :: Nil => {
        val f = io.Source.fromFile(new File(schemaFile))
        val lines = f.getLines.toList
        val len = lines.length
        val schema = if (len < 128) lines ++ defaultSchema.drop(len) else lines
        translate(data, index.toInt, schema)
      }
      case _ => { logger.error("parameter error: [{}]", options) }
    }
  }

  def translate(data: String, index: Integer, schema: List[String]) = {
    val base = URI.fromHost
    val ns = base + "/CSV#"
    val m = ModelFactory.createOntologyModel
    m.setNsPrefix(key, ns)
    m.createOntology(base)
      .addProperty(DC.date, DateTime.get, XSDdateTime)
      .addProperty(DC.description, "TriGraM CSV model", XSDstring)
      .addProperty(OWL.versionInfo, Version.get, XSDstring)

    val (rN, cN) = (schema.head, schema.drop(1))
    val ROW = m.createClass(ns + rN)
    val COL = cN map { n => m.createDatatypeProperty(ns + n) }

    val reader = new CSVReader(new FileReader(data), ';')
    val entries = Iterator.continually { reader.readNext }.takeWhile(_ != null)
    for (e <- entries) {
      val i = e(index)
      val uri = URI.fromString(i)
      val r = m.createIndividual(uri, ROW)
      (0 to e.length - 1) foreach {
        c => r.addProperty(COL(c), e(c), XSDnormalizedString)
      }
    }
    reader.close

    val output = data + "-model.owl"
    m.write(new FileOutputStream(output), "RDF/XML-ABBREV")

    logger.info("[{}] triples generated in [{}]", m.size, output)
  }
}