/**
 * DSL test
 */

import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary.{ RDF, RDFS, OWL, OWL2, DC_11 => DC, DCTerms => DT }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._

import dsl._
import modeler._
import util.{ Version, DateTime }

/**
 * @author ShiZhan
 * DSL test object
 */
object DSLModel {

  def main(args: Array[String]): Unit = {
    val model = ModelFactory.createDefaultModel
    val m = DSLElement(model)

    ((m <-- (DIR.base, OWL.Ontology))
      -- DC.date --> (DateTime.get, XSDdateTime)
      -- DC.description --> ("test model", XSDstring)
      -- DT.license --> ("Apache 2", XSDstring)
      -- OWL.versionInfo --> (Version.get, XSDstring))

    m <-- (DIR.name.getURI, OWL.DatatypeProperty)
    m <-- (DIR.size.getURI, OWL.DatatypeProperty)
    m <-- (DIR.lastModified.getURI, OWL.DatatypeProperty)
    m <-- (DIR.canRead.getURI, OWL.DatatypeProperty)
    m <-- (DIR.canWrite.getURI, OWL.DatatypeProperty)
    m <-- (DIR.canExecute.getURI, OWL.DatatypeProperty)
    m <-- (DIR.isDirectory.getURI, OWL.DatatypeProperty)

    m <-- (DIR.contain.getURI, OWL.ObjectProperty)

    (
      (m <-- (DIR.Object.getURI, OWL.Class))
      -- RDFS.subClassOf --> (
        (m <-- OWL.Restriction)
        -- OWL.onProperty --> DIR.name
        -- OWL2.cardinality --> ("1", XSDnonNegativeInteger)
        -- OWL2.onDataRange --> modeler.XSD.normalizedString)
        -- RDFS.subClassOf --> (
          (m <-- OWL.Restriction)
          -- OWL.onProperty --> DIR.size
          -- OWL2.cardinality --> ("1", XSDnonNegativeInteger)
          -- OWL2.onDataRange --> modeler.XSD.unsignedLong)
          -- RDFS.subClassOf --> (
            (m <-- OWL.Restriction)
            -- OWL.onProperty --> DIR.lastModified
            -- OWL2.cardinality --> ("1", XSDnonNegativeInteger)
            -- OWL2.onDataRange --> modeler.XSD.dateTime)
            -- RDFS.subClassOf --> (
              (m <-- OWL.Restriction)
              -- OWL.onProperty --> DIR.canRead
              -- OWL2.cardinality --> ("1", XSDnonNegativeInteger)
              -- OWL2.onDataRange --> modeler.XSD.xboolean)
              -- RDFS.subClassOf --> (
                (m <-- OWL.Restriction)
                -- OWL.onProperty --> DIR.canWrite
                -- OWL2.cardinality --> ("1", XSDnonNegativeInteger)
                -- OWL2.onDataRange --> modeler.XSD.xboolean)
                -- RDFS.subClassOf --> (
                  (m <-- OWL.Restriction)
                  -- OWL.onProperty --> DIR.canExecute
                  -- OWL2.cardinality --> ("1", XSDnonNegativeInteger)
                  -- OWL2.onDataRange --> modeler.XSD.xboolean)
                  -- RDFS.subClassOf --> (
                    (m <-- OWL.Restriction)
                    -- OWL.onProperty --> DIR.isDirectory
                    -- OWL2.cardinality --> ("1", XSDnonNegativeInteger)
                    -- OWL2.onDataRange --> modeler.XSD.xboolean)
                    -- RDFS.subClassOf --> (
                      (m <-- OWL.Restriction)
                      -- OWL.onProperty --> DIR.contain
                      -- OWL.allValuesFrom --> DIR.Object))

    println(m)

    //    m.get.write(new java.io.FileOutputStream(DIR.local), "RDF/XML-ABBREV")
  }

}