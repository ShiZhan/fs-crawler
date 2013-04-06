/**
 *
 */
package core

import scala.collection.JavaConverters._
import java.nio.file.{ Path, Files, FileSystems }
import java.nio.file.attribute.BasicFileAttributes

import com.hp.hpl.jena.rdf.model._

import com.hp.hpl.jena.vocabulary.RDF
import com.hp.hpl.jena.vocabulary.RDFS

import com.hp.hpl.jena.util.FileManager

import util.Logging

/**
 * @author ShiZhan
 *
 */
object Translator extends Logging {

  private def walkDirectory(p: Path, n: Resource, m: Model): Model = {
    val ds = Files.newDirectoryStream(p).iterator.asScala.toArray
    for (i <- ds) {
      val a = Files.readAttributes(i, classOf[BasicFileAttributes])
      println("[%s] in [%s]: %s | %s | %s ".format(
        i.getFileName, p.getFileName, a.creationTime, a.lastAccessTime, a.lastModifiedTime))
      val r = m.createResource
      // n contain r
      // r name|CT|AT|MT attributes
      if(Files.isDirectory(i)) walkDirectory(i, r, m)
    }
    m
  }

  private def modelDirectory(name: String) = {
    val p = FileSystems.getDefault.getPath(name)
    val m = ModelFactory.createDefaultModel
    if (Files.isDirectory(p)) {
      logger.info("creating model for root directory [%s]".format(p.toString))
      val n = m.createResource
      walkDirectory(p, n, m)
      m.write(System.out)
    }
    else {
      logger.info("[%s] is not a directory".format(p.toString))
    }
  }

  private def modelFile(name: String) = {
    logger.info("model file: " + name)
  }

}