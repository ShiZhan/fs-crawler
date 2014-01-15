/**
 * DMTF CIM vocabulary for OWL models
 */
package cim

/**
 * @author ShiZhan
 * Translated [DMTF CIM](http://dmtf.org/standards/cim) vocabulary
 *
 * This vocabulary is corresponding to DMTF CIM schema.
 * If new schema is published with additional concept & property,
 * then the companion file: "CIM-CLASS" and "CIM-PROPERTY" should be updated.
 */
object Vocabulary {
  import scala.xml.XML
  import com.hp.hpl.jena.rdf.model.ModelFactory
  import helper.Config.CIMDATA
  import helper.Strings._
  /*
   * prepare vocabulary model
   */
  private val model = ModelFactory.createDefaultModel

  /*
   * naming convention for CIM/OWL name space, local file name & persistent URL
   */
  val NS_PREFIX = "cim"
  val NS = "https://sites.google.com/site/ontology2013/cim/"
  def FN(n: String) = n + ".owl"
  def PURL(n: String) = NS + FN(n)
  def URI(n: String) = NS + n
  def isCimURI(uri: String) = uri.startsWith(NS)
  def URI2PURL(uri: String) = uri + ".owl"
  def PURL2FN(purl: String) = CIMDATA + '/' + purl.substring(NS.size)

  private val all = "CIM_All" // for all in one model (CimSchema)
  private val base = "CIM_Base" // for model group (CimSchemaEx)
  val FILE_ALL = FN(all)
  val FILE_BASE = FN(base)
  val PURL_ALL = NS + FILE_ALL
  val PURL_BASE = NS + FILE_BASE
  val PATH_ALL = s"$CIMDATA/$FILE_ALL"
  val PATH_BASE = s"$CIMDATA/$FILE_BASE"

  /*
   * concepts
   */
  // OWL.imports for CIM all-in-one and CIM base models
  val ALL = model.createResource(PURL_ALL)
  val BASE = model.createResource(PURL_BASE)

  // meta concepts in CIM base model
  val Meta_Class = model.createResource(URI("CIM_Meta_Class"))
  val Association = model.createResource(URI("CIM_Association"))

  // CIM classes and imports
  val classFileName = CIMDATA + "/CIM-CLASS"
  private lazy val classList =
    fromFile(classFileName) map {
      n => n -> (model.createResource(URI(n)), model.createResource(PURL(n)))
    } toMap

  private val unknown =
    (model.createResource(URI("unknown")), model.createResource(PURL("unknown")))

  def CLASS(name: String) = classList.getOrElse(name, unknown)._1
  def IMPORT(name: String) = classList.getOrElse(name, unknown)._2

  /*
   * properties
   */
  val propertyFileName = CIMDATA + "/CIM-PROPERTY"
  private lazy val propertyList =
    fromFile(propertyFileName) map { n => n -> model.createProperty(URI(n)) } toMap

  private val invalidProperty = model.createProperty(URI("invalidCimProperty"))

  def PROP(n: String) = propertyList.getOrElse(n, invalidProperty)
}