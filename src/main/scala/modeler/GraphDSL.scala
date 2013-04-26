/**
 * Domain-Specific Languages
 */
package modeler.DSL

import scala.collection.JavaConversions._
import com.hp.hpl.jena.rdf.model.{ Model, RDFNode, Resource, Property }
import com.hp.hpl.jena.datatypes.RDFDatatype

/**
 * @author ShiZhan
 * Domain-Specific Languages for intuitive modeling
 * http://ofps.oreilly.com/titles/9780596155957/DomainSpecificLanguages.html
 */
class DSLModel(m: Model) {

  def +=(other: Model) = DSLElement(other)
  def ++(r: Resource) = DSLElement(m.createResource(r))
  def ++(s: String, r: Resource) = DSLElement(m.createResource(s, r))
  def ++() = DSLElement(m.createResource)

  override def toString = m.listStatements.toList.mkString("\n")
}

class DSLResource(r: Resource) {

  def --(p: Property) = DSLElement(r, p)

}

class DSLTarget(r: Resource, p: Property) {

  def -->(n: RDFNode) = DSLElement(r.addProperty(p, n))
  def -->(s: String) = DSLElement(r.addProperty(p, s))
  def -->(s: String, t: RDFDatatype) = DSLElement(r.addProperty(p, s, t))
  def -->(s: String, uri: String) = DSLElement(r.addProperty(p, s, uri))

}

object DSLElement {

  def apply(m: Model) = new DSLModel(m)
  def apply(r: Resource) = new DSLResource(r)
  def apply(r: Resource, p: Property) = new DSLTarget(r, p)

}