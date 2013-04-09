/**
 * TriGraM translator
 */
package core

import scalax.file.{ Path, PathSet }
import java.io.FileOutputStream
import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.vocabulary.{ RDF, RDFS, OWL }
import com.hp.hpl.jena.util.FileManager
import util.Logging

/**
 * @author ShiZhan
 * translate various resources to semantic model
 */
object Translator extends Logging {

  val defaultUrl = "https://sites.google.com/site/ontology2013/trigram.owl"

  type Modeler = String => Model
  type ModelerMap = Map[String, (Modeler, String)]
  private val modelerMap: ModelerMap = Map(
    "directory" -> (modelDirectory, "Translate directory structure into TriGraM model"))
  private val modelerMapDefault = (modelUnkown _, null)

  def modelDirectory(n: String) = {
    val p = Path(n)
    if (p.isDirectory) {
      logger.info("creating model for root directory [%s]".format(p.name))

      val ps = p ***
      val model = ModelFactory.createDefaultModel
      val NAME = model.createProperty( defaultUrl + "#name" )
      val node = model.createResource( defaultUrl + "#" + n )
                       .addProperty(NAME, n)
      for (i <- ps) {
        println("[%s] in [%s]: %d|%d|%s|%s|%s".format(
          i.name, i.parent.get.name, if (i.size.nonEmpty) i.size.get else 0,
          i.lastModified, i.canRead, i.canWrite, i.canExecute))
      }

      model
    } else {
      logger.info("[%s] is not a directory".format(p.name))

      ModelFactory.createDefaultModel
    }
  }

  def modelUnkown(n: String) = {
    logger.info("unkown resource: " + n)

    ModelFactory.createDefaultModel
  }

  def run(t: String, i: String, o: String) = {
    val modeler = modelerMap.getOrElse(t, modelerMapDefault) match { case (m, s) => m }
    val model = modeler(i)
    if (model.size > 0) model.write(new FileOutputStream(o))
    println("%d triples generated".format(model.size))
  }

  val help = modelerMap.flatMap {
    case (t, (m, s)) => List("  %s: \t %s".format(t, s))
  }.mkString("\n")

}
