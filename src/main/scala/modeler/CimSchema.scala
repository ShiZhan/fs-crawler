/**
 * CIM schema modeler
 */
package modeler

import scala.xml.{ XML, NodeSeq }
import com.hp.hpl.jena.rdf.model.{ ModelFactory, Resource }
import com.hp.hpl.jena.vocabulary.{ RDF, RDFS, OWL, OWL2, DC_11 => DC, DCTerms => DT }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import util.{ Logging, Version, DateTime }

import modeler.{ CimVocabulary => CIM }

/**
 * @author ShiZhan
 * translate DMTF CIM schema [http://dmtf.org/standards/cim] into TriGraM model
 * model local file is defined in CimVocabulary as CIM.FN_ALL
 */
object CimSchema extends Modeler with Logging {

  override val key = "cim"

  override val usage = "<DMTF CIM schema> => [all-in-one OWL model]: " + CIM.PATH_ALL

  def run(options: Array[String]) = {
    val input = options(0)
    logger.info("translate [{}] from [{}] to [{}]", key, input, CIM.PATH_ALL)

    val xml = XML.loadFile(input)
    val cim = xml \\ "CIM"
    if (cim.isEmpty) {
      logger.info("input XML file doesn't contain CIM Schema")
    } else {
      val cimVer = cim.head \ "@CIMVERSION" text
      val dtdVer = cim.head \ "@DTDVERSION" text

      logger.info("[{}] version [{}] DTD version [{}]", key, cimVer, dtdVer)

      cim2owl(cim)
    }
  }

  private val dataType: Map[String, Resource] = Map(
    "string" -> XSD.xstring,
    "boolean" -> XSD.xboolean,
    "datetime" -> XSD.dateTime,
    "uint16" -> XSD.unsignedShort,
    "uint32" -> XSD.unsignedLong,
    "uint64" -> XSD.unsignedInt)

  def readDataType(t: String) = dataType.getOrElse(t, XSD.anyURI)

  def readValue(ns: NodeSeq, att: String, attName: String) =
    ("" /: ns) { (r, n) => if ((n \ att).text == attName) n.text else r }

  def cim2owl(cim: NodeSeq) = {
    val classes = cim \ "DECLARATION" \ "DECLGROUP" \ "VALUE.OBJECT" \ "CLASS"
    val rNodes = classes.flatMap(c => c \ "PROPERTY.REFERENCE")
    val pNodes = classes.flatMap(c => c \ "PROPERTY" ++ c \ "PROPERTY.ARRAY")
    val objProps = rNodes.map(_ \ "@NAME" text) distinct
    val datProps = pNodes.map(_ \ "@NAME" text) distinct

    logger.info("[{}] classes", classes.length)
    logger.info("[{}] object properties [{}] data type properties",
      objProps.length, datProps.length)

    val license = """
Copyright 2013 Shi.Zhan.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing
permissions and limitations under the License. 
"""

    val m = ModelFactory.createDefaultModel
    m.setNsPrefix(key, CIM.NS)
    m.createResource(CIM.PURL_ALL, OWL.Ontology)
      .addProperty(DC.date, DateTime.get, XSDdateTime)
      .addProperty(DC.description, "TriGraM model of CIM schema", XSDstring)
      .addProperty(DT.license, license, XSDstring)
      .addProperty(OWL.versionInfo, Version.get, XSDstring)

    val cMeta = m.createResource(CIM.Meta_Class toString, OWL.Class)
    val cAsso = m.createResource(CIM.Association toString, OWL.Class)
      .addProperty(RDFS.subClassOf, cMeta)

    for (oP <- objProps) {
      m.createProperty(CIM URI oP)
        .addProperty(RDF.`type`, OWL.ObjectProperty)
        .addProperty(RDFS.range, cMeta)
        .addProperty(RDFS.domain, cAsso)
    }

    for (dP <- datProps) {
      m.createProperty(CIM URI dP)
        .addProperty(RDF.`type`, OWL.DatatypeProperty)
        .addProperty(RDFS.domain, cMeta)
    }

    for (c <- classes) {
      val cName = (c \ "@NAME").text
      val cClass = m.createResource(CIM URI cName, OWL.Class)

      val cSuperName = (c \ "@SUPERCLASS").text
      val cQualifier = c \ "QUALIFIER"
      val cIsAsso = "true" == readValue(cQualifier, "@NAME", "Association")
      val cSuperReso =
        if (cSuperName.isEmpty)
          if (cIsAsso) cAsso else cMeta
        else
          m.getResource(CIM URI cSuperName)

      val cComment = readValue(cQualifier, "@NAME", "Description")
      val cVersion = readValue(cQualifier, "@NAME", "Version")

      cClass.addProperty(RDFS.subClassOf, cSuperReso)
        .addLiteral(RDFS.comment, cComment)
        .addLiteral(OWL.versionInfo, cVersion)

      val cReferences = c \ "PROPERTY.REFERENCE"
      for (cR <- cReferences) {
        val cRName = (cR \ "@NAME").text
        val cRClass = (cR \ "@REFERENCECLASS").text
        val cObjProp = m.getProperty(CIM URI cRName)
        val cRefReso = m.getResource(CIM URI cRClass)
        val r = m.createResource(OWL.Restriction)
          .addProperty(OWL.onProperty, cObjProp)
          .addProperty(OWL.allValuesFrom, cRefReso)

        cClass.addProperty(RDFS.subClassOf, r)

        val cRQualifier = cR \ "QUALIFIER"
        val cRComment = readValue(cRQualifier, "@NAME", "Description")

        m.createResource(OWL2.Axiom)
          .addLiteral(RDFS.comment, cRComment)
          .addProperty(OWL2.annotatedProperty, RDFS.subClassOf)
          .addProperty(OWL2.annotatedSource, cClass)
          .addProperty(OWL2.annotatedTarget, r)
      }

      val cProperties = c \ "PROPERTY" ++ c \ "PROPERTY.ARRAY"
      for (cP <- cProperties) {
        val cPName = (cP \ "@NAME").text
        val cPType = (cP \ "@TYPE").text
        val cDatProp = m.getProperty(CIM URI cPName)
        val cDatType = readDataType(cPType)
        val r = m.createResource(OWL.Restriction)
          .addProperty(OWL.onProperty, cDatProp)
          .addProperty(OWL.allValuesFrom, cDatType)

        cClass.addProperty(RDFS.subClassOf, r)

        val cPQualifier = cP \ "QUALIFIER"
        val cPComment = readValue(cPQualifier, "@NAME", "Description")

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
      m.write(new java.io.FileOutputStream(CIM.PATH_ALL), "RDF/XML-ABBREV")

      logger.info("[{}] triples generated", m.size)
    }
  }

}