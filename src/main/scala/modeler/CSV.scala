/**
 * Modeler to translate CSV file into triple model
 */
package modeler

import java.io.{ File, FileReader, FileOutputStream }
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary.{ OWL, DC_11 => DC }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
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
 * TODO: 1. all modelers add OWL.imports if uri file contains CIM concepts
 * TODO: 2. just invoke model combine, to combine those models directly,
 *       the same to other modelers, since the model combine BUG on imports is fixed.
 *       Except for those plain text translated ones, they will use import instead. 
 */
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
        logger.warn("If any of them are declared in other models such as CIM models,")
        logger.warn("they should be imported or combined if needed.")

        translate(data, index.toInt, uris)
      }
      case _ => { logger.error("parameter error: [{}]", options) }
    }
  }

  def translate(data: String, index: Integer, uris: List[String]) = {
    val base = URI.fromHost
    val m = ModelFactory.createOntologyModel
    m.createOntology(base)
      .addProperty(DC.date, DateTime.get, XSDdateTime)
      .addProperty(DC.description, "TriGraM CSV model", XSDstring)
      .addProperty(OWL.versionInfo, Version.get, XSDstring)

    val Concept = m.createClass(uris.head)
    val Properties = uris.drop(1) map { m.createDatatypeProperty(_) }

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