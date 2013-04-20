/**
 * TriGraM model vocabulary
 */
package modeler

import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary.{ XSD => JenaXSD }

/**
 * @author ShiZhan
 * TriGraM model vocabulary
 * 1. core model vocabulary
 * 2. patch XSD vocabulary from Jena, map java variables to immutable values.
 */
object TGM {

  val local = "trigram.owl"
  val base = "https://sites.google.com/site/ontology2013/" + local
  val ns = base + "#"

  private val model = ModelFactory.createDefaultModel
  val Import = model.createResource(base)

  /*
   * directory vocabulary
   */
  // class
  val Object = model.createResource(ns + "Object")

  // object property
  val contain = model.createProperty(ns + "contain")

  // data type property
  val name = model.createProperty(ns + "name")
  val size = model.createProperty(ns + "size")
  val lastModified = model.createProperty(ns + "lastModified")
  val canRead = model.createProperty(ns + "canRead")
  val canWrite = model.createProperty(ns + "canWrite")
  val canExecute = model.createProperty(ns + "canExecute")

  val isDirectory = model.createProperty(ns + "isDirectory")

}

/*
 * Patch for mapping Jena XSD var to Scala XSD val
 * conform FP fashion
 */
object XSD {

  def getURI = JenaXSD.getURI

  val xfloat = JenaXSD.xfloat
  val xdouble = JenaXSD.xdouble
  val xint = JenaXSD.xint
  val xlong = JenaXSD.xlong
  val xshort = JenaXSD.xshort
  val xbyte = JenaXSD.xbyte
  val xboolean = JenaXSD.xboolean
  val xstring = JenaXSD.xstring
  val unsignedByte = JenaXSD.unsignedByte
  val unsignedShort = JenaXSD.unsignedShort
  val unsignedInt = JenaXSD.unsignedInt
  val unsignedLong = JenaXSD.unsignedLong
  val decimal = JenaXSD.decimal
  val integer = JenaXSD.integer
  val nonPositiveInteger = JenaXSD.nonPositiveInteger
  val nonNegativeInteger = JenaXSD.nonNegativeInteger
  val positiveInteger = JenaXSD.positiveInteger
  val negativeInteger = JenaXSD.negativeInteger
  val normalizedString = JenaXSD.normalizedString
  val anyURI = JenaXSD.anyURI
  val token = JenaXSD.token
  val Name = JenaXSD.Name
  val QName = JenaXSD.QName
  val language = JenaXSD.language
  val NMTOKEN = JenaXSD.NMTOKEN
  val ENTITIES = JenaXSD.ENTITIES
  val NMTOKENS = JenaXSD.NMTOKENS
  val ENTITY = JenaXSD.ENTITY
  val ID = JenaXSD.ID
  val NCName = JenaXSD.NCName
  val IDREF = JenaXSD.IDREF
  val IDREFS = JenaXSD.IDREFS
  val NOTATION = JenaXSD.NOTATION
  val hexBinary = JenaXSD.hexBinary
  val base64Binary = JenaXSD.base64Binary
  val date = JenaXSD.date
  val time = JenaXSD.time
  val dateTime = JenaXSD.dateTime
  val duration = JenaXSD.duration
  val gDay = JenaXSD.gDay
  val gMonth = JenaXSD.gMonth
  val gYear = JenaXSD.gYear
  val gYearMonth = JenaXSD.gYearMonth
  val gMonthDay = JenaXSD.gMonthDay

}