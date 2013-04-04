/**
 * importer functions
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
 * 2013
 * import meta-data from various data sources
 */
object Importer extends Store(Store.DEFAULT_LOCATION) with Logging {

  private def walkDirectory(p: Path): Array[Path] = {
    val ds = Files.newDirectoryStream(p).iterator.asScala.toArray
    for (i <- ds) {
      val a = Files.readAttributes(i, classOf[BasicFileAttributes])
      println(i.getFileName + " in " + p.getFileName +
        " [" + a.creationTime + "|" + a.lastAccessTime + "|" + a.lastModifiedTime + "]")
    }
    ds ++ ds.filter(Files.isDirectory(_)).flatMap(walkDirectory)
  }

  private def readDirectory(d: Path): Model = {
    logger.info("initializing model with root directory: " + d.toString)

    val all = walkDirectory(d)
    println(all.size)

    ModelFactory.createDefaultModel
  }

  private def readFile(f: Path): Model = {
    logger.info("model file: " + f.toString)
    ModelFactory.createDefaultModel
  }

  private def readUnknown(f: Path): Model = {
    logger.info("unrecognized reource: " + f.toString)
    ModelFactory.createDefaultModel
  }

  def readData(name: String): Model = {
    val p = FileSystems.getDefault.getPath(name)
    if (Files.isDirectory(p)) readDirectory(p)
    else if (Files.isRegularFile(p)) readFile(p)
    else readUnknown(p)
  }

  def load(name: String) = {
    logger.info("importing RDF/OWL model")
    readData(name)
    close
  }
}
