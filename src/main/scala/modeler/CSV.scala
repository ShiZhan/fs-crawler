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
        logger.error("must provide a data source CSV and a schema CSV (delimiter: '*').")
        logger.error("data CSV:")
        logger.error("column 0:   [individual IDs]")
        logger.error("column 1~m: [property values]")
        logger.error("schema CSV:")
        logger.error("line 0:     [Concept URI,  import URI]")
        logger.error("line 1~m:   [Property URI, import URI]")
      }
    }
  }

  def loadSchema(schema: String) = {
    val reader = new CSVReader(new FileReader(schema), '*')
    val line = reader.readAll.toList map { case Array(u, i) => (u, i) }
    val concept = line.head._1
    val properties = line.drop(1).map(_._1)
    val imports = line.map(_._2).distinct
    reader.close
    (concept, properties, imports)
  }

  def translate(data: String, schema: String) = {
    val (c, p, i) = loadSchema(schema)
    println("Concept: " + c)
    println("Properties:")
    p.foreach(println)
    println("Imports:")
    i.foreach(println)
    val reader = new CSVReader(new FileReader(data), '*')
    val entries = reader.readAll
    if (!entries.isEmpty) {
      entries.toList.foreach(i => println(i.mkString(" --> ")))
    }
    reader.close

  }
}