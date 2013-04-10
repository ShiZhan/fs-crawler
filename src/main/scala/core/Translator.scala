/**
 * TriGraM translator
 */
package core

import scalax.file.{ Path, PathSet }
import java.io.FileOutputStream
import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.vocabulary.{ RDF, RDFS, OWL, OWL2 }
import com.hp.hpl.jena.vocabulary.RDF.{ `type` => TYPE }
import com.hp.hpl.jena.util.FileManager
import util.Logging

/**
 * @author ShiZhan
 * translate various resources to semantic model
 */
object Translator extends Logging {

  type Modeler = String => Model
  type ModelerMap = Map[String, (Modeler, String)]
  private val modelerMap: ModelerMap = Map(
    "directory" -> (modelDirectory, "Translate directory structure into TriGraM model"))
  private val modelerMapDefault = (modelUnkown _, null)

  def modelDirectory(n: String) = {
    val model = ModelFactory.createDefaultModel
    model.setNsPrefix("tgm", TGM.uri)

    val p = Path(n)
    if (p.isDirectory) {
      logger.info("creating model for directory [%s]".format(p.name))

      val ps = p ***
      val node = model.createResource(TGM.uri + n)
        .addProperty(TYPE, OWL2.NamedIndividual)
        .addProperty(TYPE, TGM.OBJECT)
        .addProperty(TGM.name, n)
      for (i <- ps) {
        logger.info("[%s] in [%s]: %d|%d|%s|%s|%s".format(
          i.name, i.parent.get.name, if (i.size.nonEmpty) i.size.get else 0,
          i.lastModified, i.canRead, i.canWrite, i.canExecute))
      }
    } else {
      logger.info("[%s] is not a directory".format(p.name))
    }

    model
  }

  def modelUnkown(n: String) = {
    logger.info("unkown resource: " + n)

    ModelFactory.createDefaultModel
  }

  def run(t: String, i: String, o: String) = {
    val modeler = modelerMap.getOrElse(t, modelerMapDefault) match { case (m, s) => m }
    val model = modeler(i)
    logger.info("%d triples generated".format(model.size))
    if (!model.isEmpty) {
      model.write(new FileOutputStream(o))
      logger.info("model saved to file [%s]".format(o))
    } else {
      logger.info("model is empty")
    }
  }

  val help = modelerMap.flatMap {
    case (t, (m, s)) => List("  %s: \t %s".format(t, s))
  }.mkString("\n")

}
