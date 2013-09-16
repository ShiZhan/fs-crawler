/**
 * Translated DMTF CIM vocabulary
 */
package modeler

import com.hp.hpl.jena.rdf.model.ModelFactory
import util.Config.TGMROOT

/**
 * @author ShiZhan
 * Translated [DMTF CIM](http://dmtf.org/standards/cim) vocabulary
 *
 * This vocabulary is corresponding to DMTF CIM version 2.37.0.
 * If new modeler(s) require additional concept & property,
 * then both the modeler and this common vocabulary code should be updated.
 *
 * If new concept & property are not derived from CIM,
 * then new vocabulary object should be defined to keep the code neat.
 * --- Shi.Zhan @ August 29, 2013
 */
object CimVocabulary {
  /*
   * load vocabulary files:
   * CIM-CLASS: CIM classes
   * CIM-PROPERTY: CIM properties, including references and data properties
   */
  private val cFile = io.Source.fromFile(TGMROOT + "CIM-CLASS")
  private val pFile = io.Source.fromFile(TGMROOT + "CIM-PROPERTY")
  private val cList = cFile.getLines.toList
  private val pList = pFile.getLines.toList
  cFile.close
  pFile.close

  /*
   * prepare vocabulary model
   */
  private val model = ModelFactory.createDefaultModel

  /*
   * naming convention for CIM/OWL name space, local file name & persistent URL
   */
  val NS = "https://sites.google.com/site/ontology2013/"
  def FN(n: String) = n + ".owl"
  def PURL(n: String) = NS + FN(n)
  def URI(n: String) = NS + n

  private val all = "CIM_All" // for all in one model (CimSchema)
  private val base = "CIM_Base" // for model group (CimSchemaEx)
  val FN_ALL = FN(all)
  val FN_BASE = FN(base)
  val PURL_ALL = NS + FN_ALL
  val PURL_BASE = NS + FN_BASE

  /*
   * imports
   */
  val ALL = model.createResource(PURL_ALL)
  val BASE = model.createResource(PURL_BASE)
  // imports of other individual sub-models (CimSchemaEx) will be defined while been used

  /*
   * concepts
   */
  // meta concepts
  val Meta_Class = model.createResource(URI("CIM_Meta_Class"))
  val Association = model.createResource(URI("CIM_Association"))

  // CIM schema content
  private val classList =
    cList.map {
      case n => {
        val depImport = model.createResource(PURL(n))
        val depClass = model.createResource(URI(n))
        n -> (depImport, depClass)
      }
    } toMap

  private val unknown = model.createResource

  def IMPORT(name: String) = classList.getOrElse(name, (unknown, unknown))._1
  def CLASS(name: String) = classList.getOrElse(name, (unknown, unknown))._2

  /*
   * vocabulary
   */
  private val propertyList =
    pList.map(n => n -> model.createProperty(URI(n))) toMap

  private val invalidProperty = model.createProperty(URI("invalidProperty"))

  def PROP(n: String) = propertyList.getOrElse(n, invalidProperty)
}