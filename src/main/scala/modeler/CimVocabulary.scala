/**
 * Translated DMTF CIM vocabulary
 */
package modeler

import scala.xml.XML
import com.hp.hpl.jena.rdf.model.ModelFactory
import util.Config.CIMDATA
import util.Strings.{ fromFile, strings }

/**
 * @author ShiZhan
 * Translated [DMTF CIM](http://dmtf.org/standards/cim) vocabulary
 *
 * This vocabulary is corresponding to DMTF CIM schema.
 * If new schema is published with additional concept & property,
 * then the companion file: "CIM-CLASS" and "CIM-PROPERTY" should be updated.
 * --- Shi.Zhan
 */
object CimVocabulary {
  /*
   * load vocabulary files:
   * CIM-CLASS: CIM classes
   * CIM-PROPERTY: CIM properties, including references and data properties
   */
  private val cimClassFileName = CIMDATA + "CIM-CLASS"
  private val cimPropertyFileName = CIMDATA + "CIM-PROPERTY"
  private lazy val cList = fromFile(cimClassFileName)
  private lazy val pList = fromFile(cimPropertyFileName)

  /*
   * prepare vocabulary model
   */
  private val model = ModelFactory.createDefaultModel

  /*
   * naming convention for CIM/OWL name space, local file name & persistent URL
   */
  val NS = "https://sites.google.com/site/ontology2013/cim/"
  def FN(n: String) = n + ".owl"
  def PURL(n: String) = NS + FN(n)
  def URI(n: String) = NS + n
  def isCimURI(uri: String) = uri.startsWith(NS)
  def URI2PURL(uri: String) = uri + ".owl"
  def PURL2FN(purl: String) = PATH_BASE + purl.substring(NS.size)

  private val all = "CIM_All" // for all in one model (CimSchema)
  private val base = "CIM_Base" // for model group (CimSchemaEx)
  val FILE_ALL = FN(all)
  val FILE_BASE = FN(base)
  val PURL_ALL = NS + FILE_ALL
  val PURL_BASE = NS + FILE_BASE
  val PATH_ALL = CIMDATA + FILE_ALL
  val PATH_BASE = CIMDATA + "models/"

  /*
   * concepts
   */
  // OWL.imports
  val ALL = model.createResource(PURL_ALL)
  val BASE = model.createResource(PURL_BASE)
  // meta concepts
  val Meta_Class = model.createResource(URI("CIM_Meta_Class"))
  val Association = model.createResource(URI("CIM_Association"))

  // CIM schema content
  private lazy val classList =
    cList.map {
      case n =>
        (n -> (model.createResource(URI(n)), model.createResource(PURL(n))))
    } toMap

  private val unknown =
    (model.createResource(URI("unknown")), model.createResource(PURL("unknown")))

  def CLASS(name: String) = classList.getOrElse(name, unknown)._1
  def IMPORT(name: String) = classList.getOrElse(name, unknown)._2

  /*
   * vocabulary
   */
  private lazy val propertyList =
    pList.map(n => n -> model.createProperty(URI(n))) toMap

  private val invalidProperty = model.createProperty(URI("invalidCimProperty"))

  def PROP(n: String) = propertyList.getOrElse(n, invalidProperty)

  def generator(cimSchema: String) = {
    val i = XML.loadFile(cimSchema)
    val cNodes = i \\ "CIM" \ "DECLARATION" \ "DECLGROUP" \ "VALUE.OBJECT" \ "CLASS"
    val rNodes = cNodes.flatMap(c => c \ "PROPERTY.REFERENCE")
    val pNodes = cNodes.flatMap(c => c \ "PROPERTY" ++ c \ "PROPERTY.ARRAY")
    val cNames = cNodes.map(_ \ "@NAME" text)
    val rNames = rNodes.map(_ \ "@NAME" text).distinct
    val pNames = pNodes.map(_ \ "@NAME" text).distinct
    val cimData = new java.io.File(CIMDATA)
    if (cimData.exists) assert(!cimData.isFile) else cimData.mkdir
    cNames.toFile(cimClassFileName) // beware the tailing blank line
    (rNames ++ pNames).toFile(cimPropertyFileName)
  }
}