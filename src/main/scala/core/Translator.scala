/**
 * TriGraM translator
 */
package core

import scala.collection.JavaConverters._
import java.nio.file.{ Path, Files, FileSystems }
import java.nio.file.attribute.BasicFileAttributes
import java.io.FileOutputStream

import com.hp.hpl.jena.rdf.model._

import com.hp.hpl.jena.vocabulary.RDF
import com.hp.hpl.jena.vocabulary.RDFS

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

  private def walkDirectory(p: Path): Array[Path] = {
    val ds = Files.newDirectoryStream(p).iterator.asScala.toArray
    ds ++ ds.filter(Files.isDirectory(_)).flatMap(walkDirectory)
  }

  def modelDirectory(name: String) = {
    val p = FileSystems.getDefault.getPath(name)
    if (Files.isDirectory(p)) {
      logger.info("creating model for root directory [%s]".format(p.toString))

      val m = ModelFactory.createDefaultModel
      val n = m.createResource
      for (i <- walkDirectory(p)) {
        val a = Files.readAttributes(i, classOf[BasicFileAttributes])
        println("[%s] in [%s]: %s | %s | %s ".format(
          i.getFileName, p.getFileName, a.creationTime, a.lastAccessTime, a.lastModifiedTime))
      }

      m
    } else {
      logger.info("[%s] is not a directory".format(p.toString))

      ModelFactory.createDefaultModel
    }
  }

  def modelUnkown(name: String) = {
    logger.info("unkown resource: " + name)

    ModelFactory.createDefaultModel
  }

  def run(t: String, i: String, o: String) = {
    val modeler = modelerMap.getOrElse(t, (modelUnkown _, null)) match { case (m, s) => m }
    val model = modeler(i)
    if (model.size > 0) model.write(new FileOutputStream(o))
    println("%d triples generated".format(model.size))
  }

  val help = modelerMap.flatMap {
    case (t, (m, s)) => List("  %s: \t %s".format(t, s))
  }.mkString("\n")

}
