/**
 * Domain-Specific Languages
 */
package dsl

import scala.collection.JavaConversions._
import com.hp.hpl.jena.rdf.model.{ Model, RDFNode, Resource, Property }
import com.hp.hpl.jena.datatypes.RDFDatatype

/**
 * @author ShiZhan
 * Domain-Specific Languages for intuitive modeling
 */
class DSLModel(m: Model) {

  def +=(other: Model) = DSLElement(m.add(other))
  def <=(other: Model) = DSLElement(m.union(other))

  def <--(r: Resource) = DSLElement(m.createResource(r))
  def <--(s: String, r: Resource) = DSLElement(m.createResource(s, r))
  def <--(s: String) = DSLElement(m.createResource(s))
  def <--() = DSLElement(m.createResource)

  def <-@(s: String) = DSLElement(m.createProperty(s))

  def get = m

  override def toString = m.listStatements.toList.mkString("\n")

}

class DSLProperty(p: Property) {

  def get = p

}

class DSLResource(r: Resource) {

  def --(p: Property) = DSLElement(r, p)

  def get = r

}

class DSLTarget(r: Resource, p: Property) {

  def -->(n: DSLResource) = DSLElement(r.addProperty(p, n get))
  def -->(n: RDFNode) = DSLElement(r.addProperty(p, n))
  def -->(s: String) = DSLElement(r.addProperty(p, s))
  def -->(s: String, t: RDFDatatype) = DSLElement(r.addProperty(p, s, t))
  def -->(s: String, uri: String) = DSLElement(r.addProperty(p, s, uri))

}

object DSLElement {

  def apply(m: Model) = new DSLModel(m)
  def apply(r: Resource) = new DSLResource(r)
  def apply(p: Property) = new DSLProperty(p)
  def apply(r: Resource, p: Property) = new DSLTarget(r, p)

}