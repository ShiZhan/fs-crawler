/**
 * TriGraM vocabulary
 */
package modeler

import com.hp.hpl.jena.rdf.model.ModelFactory

/**
 * @author ShiZhan
 * TriGraM vocabulary
 */
object Vocabulary {
  import com.hp.hpl.jena.rdf.model.ModelFactory

  private val model = ModelFactory.createDefaultModel

  val NS_PREFIX = "tgm"
  val NS = "https://sites.google.com/site/trigram/"

  private val cMap = Seq("File", "Directory")
    .map { c => c -> model.createResource(NS + c) }.toMap
  private val pMap = Seq("name", "fileSize", "lastMod",
    "canRead", "canWrite", "canExecute", "isDirectory", "md5",
    "contains")
    .map { p => p -> model.createProperty(NS + p) }.toMap

  private val invalidClass = model.createResource(NS + "InvalidClass")
  private val invalidProperty = model.createProperty(NS + "InvalidProperty")

  def CLASS(n: String) = cMap.getOrElse(n, invalidClass)
  def PROP(n: String) = pMap.getOrElse(n, invalidProperty)
}