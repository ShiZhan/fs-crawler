/**
 * CIM schema modeler
 */
package modeler

import java.io.{ File, FileOutputStream }
import scala.xml.{ XML, NodeSeq }
import com.hp.hpl.jena.rdf.model.{ ModelFactory, Resource }
import com.hp.hpl.jena.vocabulary.{ RDF, RDFS, OWL, OWL2, DC_11 => DC, DCTerms => DT }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import util.{ Logging, Version, DateTime }

import modeler.{ CimVocabulary => CIM }
import CimSchema.{ readValue, readDataType }

/**
 * @author ShiZhan
 * translate DMTF CIM schema [http://dmtf.org/standards/cim] into TriGraM model
 *
 * rather than put all the concepts and properties as a whole, this modeler will
 * translate the all-in-one XML schema into individual yet dependent models.
 * so the model group can be sized according to specific application domain.
 */
object CimSchemaEx extends Modeler with Logging {

  override val key = "cimex"

  override val usage = "Translate DMTF CIM schema to a group of inter-related models"

  def run(input: String, output: String) = {
    logger.info("translate [{}] from [{}] to model group [{}]", key, input, output)

    val xml = XML.loadFile(input)
    val cim = xml \\ "CIM"
    if (cim.isEmpty) {
      logger.info("input XML file doesn't contain CIM Schema")
    } else {
      val cimVer = cim.head \ "@CIMVERSION" text
      val dtdVer = cim.head \ "@DTDVERSION" text

      logger.info("[{}] version [{}] DTD version [{}]", key, cimVer, dtdVer)

      cim2owl_group(cim, output)
    }
  }

  def cim2owl_group(cim: NodeSeq, output: String) = {
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

    // create & fill the model
    val baseModel = ModelFactory.createDefaultModel
    baseModel.setNsPrefix(key, CIM.NS)
    baseModel.createResource(CIM.PURL_BASE, OWL.Ontology)
      .addProperty(DC.date, DateTime.get, XSDdateTime)
      .addProperty(DC.description, "CIM Base model", XSDstring)
      .addProperty(DT.license, license, XSDstring)
      .addProperty(OWL.versionInfo, Version.get, XSDstring)

    // name of these two meta-concepts should be consistent globally
    // so invoke the predefined resources directly
    val cMeta = baseModel.createResource(CIM.Meta_Class toString, OWL.Class)
    val cAsso = baseModel.createResource(CIM.Association toString, OWL.Class)
      .addProperty(RDFS.subClassOf, cMeta)

    // all models should be put here
    val givenPath = new File(output)
    val repo =
      if (givenPath.exists)
        if (givenPath.isFile)
          ""
        else
          output
      else {
        givenPath.mkdir
        output
      }

    // write base concepts to one local model file, for direct import later.
    val baseStore = new FileOutputStream(new File(repo, CIM.FN_BASE))
    baseModel.write(baseStore, "RDF/XML-ABBREV")

    // iterate though all classes and create individual models accordingly.
    // CIM 2.37.0 contains [1799] classes
    for (c <- classes) {
      // gathering data from input XML model
      val cName = (c \ "@NAME").text
      val cSuperName = (c \ "@SUPERCLASS").text
      val cQualifier = c \ "QUALIFIER"
      val cIsAsso = "true" == readValue(cQualifier, "@NAME", "Association")
      val cComment = readValue(cQualifier, "@NAME", "Description")
      val cVersion = readValue(cQualifier, "@NAME", "Version")

      logger.info("modelling class [{}]", cName)

      // create & initialize the model
      val m = ModelFactory.createDefaultModel

      val cImport =
        if (cSuperName.isEmpty)
          CIM.BASE
        else
          CIM.IMPORT(cSuperName)

      m.setNsPrefix(key, CIM.NS)
      m.createResource(CIM.PURL(cName), OWL.Ontology)
        .addProperty(DC.date, DateTime.get, XSDdateTime)
        .addProperty(DC.description, s"TriGraM model of $cName", XSDstring)
        .addProperty(DT.license, license, XSDstring)
        .addProperty(OWL.versionInfo, Version.get, XSDstring)
        .addProperty(OWL.imports, cImport)

      val cClass = m.createResource(CIM.URI(cName), OWL.Class)
      val cSuperRes =
        if (cSuperName.isEmpty)
          if (cIsAsso) cAsso else cMeta
        else
          CIM.CLASS(cSuperName)

      cClass.addProperty(RDFS.subClassOf, cSuperRes)
        .addLiteral(RDFS.comment, cComment)
        .addLiteral(OWL.versionInfo, cVersion)

      val cReferences = c \ "PROPERTY.REFERENCE"
      for (cR <- cReferences) {
        val cRName = (cR \ "@NAME").text
        val cRClass = (cR \ "@REFERENCECLASS").text
        val cObjProp = m.getProperty(CIM URI cRName)
          .addProperty(RDF.`type`, OWL.ObjectProperty)
        // put additional property description here, according to CIM Qualifiers
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
          .addProperty(RDF.`type`, OWL.DatatypeProperty)
        // put additional property description here, according to CIM Qualifiers
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

      // write the model
      val cStore = new FileOutputStream(new File(repo, CIM.FN(cName)))
      m.write(cStore, "RDF/XML-ABBREV")

      logger.info("[{}] triples written to [{}]", m.size, CIM.FN(cName))
    }

  }

}