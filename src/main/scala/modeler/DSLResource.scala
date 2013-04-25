/**
 *
 */
package modeler

import com.hp.hpl.jena.rdf.model.{ Model, RDFNode, Resource, Property }
import com.hp.hpl.jena.datatypes.RDFDatatype

/**
 * @author ShiZhan
 *
 */
trait DSLResource extends Resource {

  def --(p: Property) = new DSLTarget(this, p)

}

class DSLTarget(r: Resource, p: Property) {

  def -->(n: RDFNode) = r.addProperty(p, n).asInstanceOf[DSLResource]
  def -->(s: String) = r.addProperty(p, s).asInstanceOf[DSLResource]
  def -->(s: String, t: RDFDatatype) = r.addProperty(p, s, t).asInstanceOf[DSLResource]
  def -->(s: String, uri: String) = r.addProperty(p, s, uri).asInstanceOf[DSLResource]

}