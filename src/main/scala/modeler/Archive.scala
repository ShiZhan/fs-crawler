/**
 * Modeler for compressed resources
 */
package modeler

import org.apache.commons.compress.archivers._
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.vocabulary.{ RDF, RDFS, OWL, OWL2, DC_11 => DC, DCTerms => DT }
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype._
import util.{ Logging, Version, DateTime, Hash }

/**
 * @author ShiZhan
 * translate archive file contents into semantic model
 * can be used with Directory modeler to reveal the detail of a file system
 */
object ARC {

  val local = "tgm" + Archive.key + ".owl"
  val base = "https://sites.google.com/site/ontology2013/" + local
  val ns = base + "#"

  private val model = ModelFactory.createDefaultModel
  val Import = model.createResource(base)

  val ArchiveFile = model.createResource(ns + "ArchiveFile")
  val ArchiveEntry = model.createResource(ns + "ArchiveEntry")

  val name = model.createProperty(ns + "name")
  val size = model.createProperty(ns + "size")
  val hasEntry = model.createProperty(ns + "hasEntry")

}

object Archive extends Modeler with Logging {

  override val key = "arc"

  override val usage = "Translate archive file contents"

  def tBox = {
    logger.info("initialize core model")

    val license = """
Copyright 2013 Shi.Zhan.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing
permissions and limitations under the License. 
"""

    val m = ModelFactory.createDefaultModel

    m.setNsPrefix(key, ARC.ns)
    m.createResource(ARC.base, OWL.Ontology)
      .addProperty(DC.date, DateTime.get, XSDdateTime)
      .addProperty(DC.description, "TriGraM Zipped Archive model", XSDstring)
      .addProperty(DT.license, license, XSDstring)
      .addProperty(OWL.versionInfo, Version.get, XSDstring)


    m.write(new java.io.FileOutputStream(ARC.local), "RDF/XML-ABBREV")

    logger.info("created [%d] triples in TBox [%s]".format(m.size, ARC.local))
  }

  def aBox(i: String, o: String) = {
    logger.info("Model zipped files")
  }

}