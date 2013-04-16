/**
 * Modeler to translate directory
 */
package modeler

import scalax.file.{ Path, PathSet }
import com.hp.hpl.jena.rdf.model.{ ModelFactory, Model }
import util.{ Logging, Version, DateTime, Hash }

/**
 * @author ShiZhan
 * translate HUGE directory structure into TriGraM model
 * using string interpolation
 */
object DirectoryEx extends Modeler with Logging {

  private val license = """
Copyright 2013 Shi.Zhan.
Licensed under the Apache License, Version 2.0 (the &quot;License&quot;);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing
permissions and limitations under the License. 
"""

  private val headerT = """
<rdf:RDF
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:tgm="https://sites.google.com/site/ontology2013/trigram.owl#"
    xmlns:owl="http://www.w3.org/2002/07/owl#"
    xmlns:dc="http://purl.org/dc/elements/1.1/">
  <owl:Ontology rdf:about="http://localhost/directory/temp">
    <owl:imports rdf:resource="https://sites.google.com/site/ontology2013/trigram.owl"/>
    <owl:versionInfo rdf:datatype="http://www.w3.org/2001/XMLSchema#string"
    >version 0.1 beta (source repo not available)</owl:versionInfo>
    <dc:description rdf:datatype="http://www.w3.org/2001/XMLSchema#string"
    >TriGraM directory model</dc:description>
    <dc:date rdf:datatype="http://www.w3.org/2001/XMLSchema#dateTime"
    >2013-04-16T22:07:54</dc:date>
  </owl:Ontology>
"""

  private val footerT = "</rdf:RDF>"

  private val individualT = """
  <owl:NamedIndividual rdf:about="http://localhost/directory/temp#3d801aa532c1cec3ee82d87a99fdf63f">
    <tgm:canRead rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean"
    >true</tgm:canRead>
    <tgm:lastModified rdf:datatype="http://www.w3.org/2001/XMLSchema#dateTime"
    >2013-04-16T21:11:44</tgm:lastModified>
    <tgm:name rdf:datatype="http://www.w3.org/2001/XMLSchema#normalizedString"
    >temp</tgm:name>
    <tgm:canWrite rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean"
    >true</tgm:canWrite>
    <tgm:canExecute rdf:datatype="http://www.w3.org/2001/XMLSchema#boolean"
    >true</tgm:canExecute>
    <tgm:size rdf:datatype="http://www.w3.org/2001/XMLSchema#unsignedLong"
    >0</tgm:size>
    <rdf:type rdf:resource="https://sites.google.com/site/ontology2013/trigram.owl#Object"/>

  </owl:NamedIndividual>
"""
  private val containT = """
    <tgm:contain rdf:resource="http://localhost/directory/temp#a4252f3f211ffd9be44fdf33feda5ef2"/>
"""

  def usage = "Translate *HUGE* directory structure into TriGraM model"

  // the same with Directory modeler, no need to add more statements.
  def core = ModelFactory.createDefaultModel

  def translate(i: String, o: String) = {
    val p = Path(i)
    val ps = p.***

    logger.info("creating model for *HUGE* directory")
  }

}