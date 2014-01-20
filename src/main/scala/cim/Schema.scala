/**
 * CIM schema modeler
 */
package cim

/**
 * @author ShiZhan
 * translate DMTF CIM schema [http://dmtf.org/standards/cim] into TriGraM model
 * 1. local model file: CIM.FN_ALL, contains all concepts and properties.
 * 2. dependent model group: in directory CIM.BASE, can be flexible.
 */
object Schema extends helper.Logging {
  import java.io.File
  import scala.xml.{ Elem, Node, NodeSeq, XML }
  import com.hp.hpl.jena.rdf.model.{ Model, ModelFactory, Resource }
  import com.hp.hpl.jena.vocabulary.{ RDF, RDFS, OWL, OWL2, DC_11 => DC, DCTerms => DT }
  import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
  import com.hp.hpl.jena.vocabulary.XSD
  import cim.{ Vocabulary => CIM }
  import common.ArchiveEx.InputStreamAsArchiveStream
  import common.ModelEx._
  import common.StringSeqEx._
  import helper.{ BuildIn, Config, DateTime, Version }

  private val dataType: Map[String, Resource] = Map(
    "string" -> XSD.xstring,
    "boolean" -> XSD.xboolean,
    "datetime" -> XSD.dateTime,
    "uint16" -> XSD.unsignedShort,
    "uint32" -> XSD.unsignedLong,
    "uint64" -> XSD.unsignedInt)

  private def readDataType(t: String) = dataType.getOrElse(t, XSD.anyURI)

  private def readValue(ns: NodeSeq, att: String, attName: String) =
    ("" /: ns) { (r, n) => if ((n \ att).text == attName) n.text else r }

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

  implicit class CIM_CLASS(c: Node) {
    val cName = (c \ "@NAME").text
    val cSuperName = (c \ "@SUPERCLASS").text
    val cQualifier = c \ "QUALIFIER"
    val cIsAsso = "true" == readValue(cQualifier, "@NAME", "Association")
    val cComment = readValue(cQualifier, "@NAME", "Description")
    val cVersion = readValue(cQualifier, "@NAME", "Version")
    val cImport = if (cSuperName.isEmpty) CIM.BASE else CIM.IMPORT(cSuperName)

    def >>(m: Model) = {
      val cClass = m.createResource(CIM.URI(cName), OWL.Class)
      val cSuperRes =
        if (cSuperName.isEmpty)
          if (cIsAsso) CIM.Association else CIM.Meta_Class
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
    }

    def saveAsModel = {
      val m = ModelFactory.createDefaultModel
      m.setNsPrefix(CIM.NS_PREFIX, CIM.NS)
      m.createResource(CIM.PURL(cName), OWL.Ontology)
        .addProperty(DC.date, DateTime.get, XSDdateTime)
        .addProperty(DC.description, s"TriGraM model of $cName", XSDstring)
        .addProperty(DT.license, license, XSDstring)
        .addProperty(OWL.versionInfo, Version.get, XSDstring)
        .addProperty(OWL.imports, cImport)

      >>(m)

      m.store(new File(Config.CIMDATA, CIM.FN(cName)))
    }
  }

  case class CIMXML(cim: NodeSeq) {
    val classes = cim \ "DECLARATION" \ "DECLGROUP" \ "VALUE.OBJECT" \ "CLASS"
    val rNodes = classes.flatMap(c => c \ "PROPERTY.REFERENCE")
    val pNodes = classes.flatMap(c => c \ "PROPERTY" ++ c \ "PROPERTY.ARRAY")
    val objProps = rNodes.map(_ \ "@NAME" text) distinct
    val datProps = pNodes.map(_ \ "@NAME" text) distinct

    logger.info("[{}] classes", classes.length)
    logger.info("[{}] object properties [{}] data type properties",
      objProps.length, datProps.length)

    // store vocabulary for Vocabulary to load
    classes.map(_ \ "@NAME" text).toFile(CIM.classFileName)
    (objProps ++ datProps).toFile(CIM.propertyFileName)

    def toModel = {
      val m = ModelFactory.createDefaultModel
      m.setNsPrefix(CIM.NS_PREFIX, CIM.NS)
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

      for (c <- classes) c >> m

      m.store(CIM.PATH_ALL)
    }

    def toModelGroup = {
      // create CIM_Base.owl for import both CIM_Meta_Class & CIM_Association
      val baseModel = ModelFactory.createDefaultModel
      baseModel.setNsPrefix(CIM.NS_PREFIX, CIM.NS)
      baseModel.createResource(CIM.PURL_BASE, OWL.Ontology)
        .addProperty(DC.date, DateTime.get, XSDdateTime)
        .addProperty(DC.description, "CIM Base model", XSDstring)
        .addProperty(DT.license, license, XSDstring)
        .addProperty(OWL.versionInfo, Version.get, XSDstring)

      val cMeta = baseModel.createResource(CIM.Meta_Class toString, OWL.Class)
      baseModel.createResource(CIM.Association toString, OWL.Class)
        .addProperty(RDFS.subClassOf, cMeta)

      baseModel.store(CIM.PATH_BASE)

      // iterate though all classes and create individual models accordingly.
      classes.par foreach { _.saveAsModel }
    }
  }

  def validateSchema(xml: Elem) = {
    val cim = xml \\ "CIM"
    if (cim.isEmpty) null
    else {
      val cimVer = { cim.head \ "@CIMVERSION" text }
      val dtdVer = { cim.head \ "@DTDVERSION" text }
      logger.info("CIM version: [{}]", cimVer)
      logger.info("DTD version: [{}]", dtdVer)
      CIMXML(cim)
    }
  }

  def fromXML(sArgs: List[String]) = {
    val xml = if (sArgs == Nil) {
      logger.info("Loading default DMTF CIM Schema")
      XML.load(BuildIn.get("all_classes.xml.bz2").asBzip)
    } else {
      XML.loadFile(sArgs.head)
    }
    validateSchema(xml)
  }

  private def getBaseURI(owlFile: File) = {
    val buf = io.Source.fromFile(owlFile)
    val txt = buf.getLines.filter { _.contains("<owl:Ontology rdf:about=") }.mkString
    buf.close
    """\".*\"""".r.findFirstIn(txt)
  }

  def check(cArgs: List[String]) = {
    val cList = fromFile(CIM.classFileName)
    val pList = fromFile(CIM.propertyFileName)
    if (cArgs == Nil) {
      val owls = new File(Config.CIMDATA).listFiles.filter(_.getName.endsWith(".owl"))
      val (invalidURIs, validURIs) = owls.map(getBaseURI).partition(None ==)
      println("Vocabulary: " + cList.size + " classes " + pList.size + " properties")
      println("CIM Models: " + validURIs.length + " valid " + invalidURIs.length + " invalid")
    } else {
      println("Checking CIM class model(s): " + cArgs.mkString(", "))
      for (c <- cArgs) {
        if (cList.contains(c)) {
          val ffn = CIM.FFN(c)
          val size = load(ffn).size
          if (0 == size) println(ffn + " isn't generated (use '-s').")
          else println(ffn + " has " + size + " triples.")
        } else println(c + " isn't in current CIM vocabulary.")
      }
    }
  }
}