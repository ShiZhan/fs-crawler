/**
 * CIM schema modeler
 */
package modeler

import scala.xml.{ XML, NodeSeq }
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary.{ RDF, RDFS, OWL, OWL2, DC_11 => DC, DCTerms => DT }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import util.{ Logging, Version, DateTime }

/**
 * @author ShiZhan
 * translate DMTF CIM schema [http://dmtf.org/standards/cim] into TriGraM model
 */
object CIM {

  val local = "tgm" + CimModeler.key + ".owl"
  val base = "https://sites.google.com/site/ontology2013/" + local
  val ns = base + "#"

  def ##(name: String) = ns + name
}

object CimModeler extends Modeler with Logging {

  override val key = "cim"

  override val usage = "Translate DMTF CIM schema http://dmtf.org/standards/cim"

  def tBox = {
    logger.info("prepare meta-model for CIM schema translation")

    logger.info("TODO: create a meta-model to translate the detail of qualifiers")
  }

  def aBox(input: String, output: String) = {
    logger.info("translate CIM schema from [" + input + "] to [" + output + "]")

    val xml = XML.loadFile(input)
    val classes = xml \\ "VALUE.OBJECT" \ "CLASS"
    val rNodes = classes.flatMap(c => c \ "PROPERTY.REFERENCE")
    val pNodes = classes.flatMap(c => c \ "PROPERTY" ++ c \ "PROPERTY.ARRAY")
    val objProps = rNodes.map(r => (r \ "@NAME").text).distinct
    val datProps = pNodes.map(p => (p \ "@NAME").text).distinct

    logger.info("[%d] classes [%d] object properties [%d] data type properties".
      format(classes.length, objProps.length, datProps.length))

    val license = """
Copyright 2013 Shi.Zhan.
Licensed under the Apache License, Version 2.0 (the &quot;License&quot;);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing
permissions and limitations under the License. 
"""

    val m = ModelFactory.createDefaultModel
    m.setNsPrefix(key, CIM.ns)
    m.createResource(CIM.base, OWL.Ontology)
      .addProperty(DC.date, DateTime.get, XSDdateTime)
      .addProperty(DC.description, "TriGraM model of CIM schema", XSDstring)
      .addProperty(DT.license, license, XSDstring)
      .addProperty(OWL.versionInfo, Version.get, XSDstring)

    val cMeta = m.createResource(CIM ## "CIM_Meta_Class", OWL.Class)
    val cAsso = m.createResource(CIM ## "CIM_Association", OWL.Class)
      .addProperty(RDFS.subClassOf, cMeta)

    for (oP <- objProps) {
      m.createProperty(CIM ## oP)
        .addProperty(RDF.`type`, OWL.ObjectProperty)
        .addProperty(RDFS.range, cMeta)
        .addProperty(RDFS.domain, cAsso)
    }

    for (dP <- datProps) {
      m.createProperty(CIM ## dP)
        .addProperty(RDF.`type`, OWL.DatatypeProperty)
        .addProperty(RDFS.domain, cMeta)
    }

    def getQualifier(ns: NodeSeq, qname: String) =
      ("" /: ns) { (r, q) =>
        if ((q \ "@NAME").text == qname) q.text
        else r
      }

    for (c <- classes) {
      val cName = (c \ "@NAME").text
      val cSuperName = (c \ "@SUPERCLASS").text
      val cQualifier = c \ "QUALIFIER"
      val cIsAsso = cQualifier.map(q => (q \ "@NAME").text).contains("Association")

      val cSuper =
        if (cSuperName.isEmpty)
          if (cIsAsso) cAsso else cMeta
        else
          m.getResource(CIM ## cSuperName)

      val cComment = getQualifier(cQualifier, "Description")
      val cVersion = getQualifier(cQualifier, "Version")

      val cClass = m.createResource(CIM ## cName, OWL.Class)
        .addProperty(RDFS.subClassOf, cSuper)
        .addLiteral(RDFS.comment, cComment)
        .addLiteral(OWL.versionInfo, cVersion)
    }

    if (m.isEmpty)
      logger.info("Nothing translated")
    else {
      m.write(new java.io.FileOutputStream(output), "RDF/XML-ABBREV")

      logger.info("[%d] triples generated".format(m.size))
    }
  }

}