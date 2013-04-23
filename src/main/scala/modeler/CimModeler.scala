/**
 * CIM schema modeler
 */
package modeler

import scala.xml.{ XML, NodeSeq }
import com.hp.hpl.jena.rdf.model.{ ModelFactory, Resource }
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

  private val dataType: Map[String, Resource] = Map(
    "string" -> XSD.xstring,
    "boolean" -> XSD.xboolean,
    "datetime" -> XSD.dateTime,
    "uint16" -> XSD.unsignedShort,
    "uint32" -> XSD.unsignedLong,
    "uint64" -> XSD.unsignedInt)

  def ^^(t: String) = dataType.getOrElse(t, XSD.anyURI)

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

    def pickNodeValue(ns: NodeSeq, att: String, attName: String) =
      ("" /: ns) { (r, q) => if ((q \ att).text == attName) q.text else r }

    for (c <- classes) {
      val cName = (c \ "@NAME").text
      val cClass = m.createResource(CIM ## cName, OWL.Class)

      val cSuperName = (c \ "@SUPERCLASS").text
      val cQualifier = c \ "QUALIFIER"
      val cIsAsso = "true" == pickNodeValue(cQualifier, "@NAME", "Association")
      val cSuperReso =
        if (cSuperName.isEmpty)
          if (cIsAsso) cAsso else cMeta
        else
          m.getResource(CIM ## cSuperName)

      val cComment = pickNodeValue(cQualifier, "@NAME", "Description")
      val cVersion = pickNodeValue(cQualifier, "@NAME", "Version")

      cClass.addProperty(RDFS.subClassOf, cSuperReso)
        .addLiteral(RDFS.comment, cComment)
        .addLiteral(OWL.versionInfo, cVersion)

      val cReferences = c \ "PROPERTY.REFERENCE"
      for (cR <- cReferences) {
        val cRName = (cR \ "@NAME").text
        val cRClass = (cR \ "@REFERENCECLASS").text
        val cObjProp = m.getProperty(CIM ## cRName)
        val cRefReso = m.getResource(CIM ## cRClass)
        val r = m.createResource(OWL.Restriction)
          .addProperty(OWL.onProperty, cObjProp)
          .addProperty(OWL.allValuesFrom, cRefReso)

        cClass.addProperty(RDFS.subClassOf, r)
      }

      val cProperties = c \ "PROPERTY" ++ c \ "PROPERTY.ARRAY"
      for (cP <- cProperties) {
        val cPName = (cP \ "@NAME").text
        val cPType = (cP \ "@TYPE").text
        val cDatProp = m.getProperty(CIM ## cPName)
        val cDatType = CIM ^^ cPType
        val r = m.createResource(OWL.Restriction)
          .addProperty(OWL.onProperty, cDatProp)
          .addProperty(OWL.allValuesFrom, cDatType)

        cClass.addProperty(RDFS.subClassOf, r)

        val cPQualifier = cP \ "QUALIFIER"
        val cPComment = pickNodeValue(cPQualifier, "@NAME", "Description")

        m.createResource(OWL2.Axiom)
          .addLiteral(RDFS.comment, cPComment)
          .addProperty(OWL2.annotatedProperty, RDFS.subClassOf)
          .addProperty(OWL2.annotatedSource, cClass)
          .addProperty(OWL2.annotatedTarget, r)
      }
    }

    if (m.isEmpty)
      logger.info("Nothing translated")
    else {
      m.write(new java.io.FileOutputStream(output), "RDF/XML-ABBREV")

      logger.info("[%d] triples generated".format(m.size))
    }
  }

}