/**
 * Modeler to translate CSV file into triple model
 */
package modeler

import java.io.{ FileReader, FileOutputStream }
import scala.collection.JavaConversions._
import au.com.bytecode.opencsv.CSVReader
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary.{ RDF, RDFS, OWL, OWL2, DC_11 => DC, DCTerms => DT }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import util.{ Logging, Version, DateTime, Hash }

import modeler.{ CimVocabulary => CIM }

/**
 * @author ShiZhan
 * Translate CSV into triple model
 * A wide range support for various data sources that can be represented by CSV
 */
object CSV extends Modeler with Logging {
  override val key = "csv"

  override val usage = "<CSV> <schema> => [triples]"

  def run(options: Array[String]) = {
    options.toList match {
      case data :: schema :: tail => translate(data, schema)
      case _ => {
        logger.error("parameter error: [{}]", options)
        logger.error("must provide a data source CSV and a schema CSV.")
        logger.error("schema CSV:")
        logger.error("line 0:   [Concept URI,  import URI]")
        logger.error("line 1~m: [Property URI, import URI]")
      }
    }
  }

  def translate(data: String, schema: String) = {
    val schemaReader = new CSVReader(new FileReader(schema))
    val schemaList = schemaReader.readAll
    val cLine = schemaList.head
    val pLines = schemaList.drop(1).toList
    schemaReader.close

    val reader = new CSVReader(new FileReader(data))
    val entries = reader.readAll
    if (!entries.isEmpty) {
      entries.toList.foreach(i => println(i.mkString))
    }
    reader.close

  }
}