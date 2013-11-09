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
import util.{ Logging, Version, DateTime, URI }

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
        logger.error("must provide a data source CSV (delimiter: '*') and a schema.")
        logger.error("data CSV:")
        logger.error("column 0:   [individual IDs]")
        logger.error("column 1~m: [property values]")
        logger.error("schema:")
        logger.error("line 0:     [CIM Class]")
        logger.error("line 1~m:   [CIM Property]")
      }
    }
  }

  def loadSchema(schema: String) = {
    val s = io.Source.fromFile(schema)
    val lines = s.getLines.toList
    val concept = lines.head
    val properties = lines.drop(1)
    s.close
    (concept, properties)
  }

  private def initModel(i: String) = {
    val base = URI.fromHost
    val ns = base + "#"
    val m = ModelFactory.createDefaultModel
    m.setNsPrefix(key, ns)
    m.setNsPrefix(CimSchema.key, CIM.NS)
    m.createResource(base, OWL.Ontology)
      .addProperty(DC.date, DateTime.get, XSDdateTime)
      .addProperty(DC.description, "TriGraM CSV model", XSDstring)
      .addProperty(OWL.versionInfo, Version.get, XSDstring)
      .addProperty(OWL.imports, CIM.IMPORT(i))
    m
  }

  def translate(data: String, schema: String) = {
    val (c, p) = loadSchema(schema)
    val m = initModel(c)
    val reader = new CSVReader(new FileReader(data), '*')
    val entries = reader.readAll
    for (e <- entries) {
      println(e.mkString(" --> "))
    }
    reader.close
  }
}