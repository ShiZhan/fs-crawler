/**
 * Modeler to translate directory
 */
package modeler

import scalax.file.{ Path, PathSet }
import com.hp.hpl.jena.rdf.model.{ ModelFactory, Model }
import util.{ Logging, Version, DateTime, Hash }

/**
 * @author ShiZhan
 * translate HUGE directory structure into TriGraM model
 * using string interpolation
 */
object DirectoryEx extends Modeler with Logging {

  private def headerT =
    (trigramBase: String, base: String, version: String, dateTime: String) => s"""
<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:tgm="$trigramBase#"
    xmlns:owl="http://www.w3.org/2002/07/owl#"
    xmlns:dc="http://purl.org/dc/elements/1.1/">
  <owl:Ontology rdf:about="$base">
    <owl:imports rdf:resource="$trigramBase"/>
    <owl:versionInfo rdf:datatype="http://www.w3.org/2001/XMLSchema#string"
    >$version</owl:versionInfo>
    <dc:description rdf:datatype="http://www.w3.org/2001/XMLSchema#string"
    >TriGraM directory model</dc:description>
    <dc:date rdf:datatype="http://www.w3.org/2001/XMLSchema#dateTime"
    >$dateTime</dc:date>
  </owl:Ontology>
"""

  private def containT = (base: String, nodeId: String) => s"""
    <tgm:contain rdf:resource="$base#$nodeId"/>
"""

  private def individualT =
    (base: String, nodeId: String, trigramBase: String, contains: String,
      name: String, size: Long, lastModified: String,
      canRead: Boolean, canWrite: Boolean, canExecute: Boolean) => s"""
  <owl:NamedIndividual rdf:about="$base#$nodeId">
    <rdf:type rdf:resource="$trigramBase#Object"/>
    <tgm:name rdf:datatype="http://www.w3.org/2001/XMLSchema#normalizedString"
    >$name</tgm:name>
    <tgm:size rdf:datatype="http://www.w3.org/2001/XMLSchema#unsignedLong"
    >$size</tgm:size>
    <tgm:lastModified rdf:datatype="http://www.w3.org/2001/XMLSchema#dateTime"
    >$lastModified</tgm:lastModified>
    <tgm:canRead rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean"
    >$canRead</tgm:canRead>
    <tgm:canWrite rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean"
    >$canWrite</tgm:canWrite>
    <tgm:canExecute rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean"
    >$canExecute</tgm:canExecute>
  $contains
  </owl:NamedIndividual>
"""

  private val footerT = "</rdf:RDF>"

  def usage = "Translate *HUGE* directory structure into TriGraM model"

  // the same with Directory modeler, no need to add more statements.
  def core = ModelFactory.createDefaultModel

  def translate(i: String, o: String) = {
    val p = Path(i)
    val ps = p.***

    logger.info("creating model for *HUGE* directory")

    val m = new java.io.FileOutputStream(o, true)

    m.write("hello".getBytes)

    m.close
  }

}