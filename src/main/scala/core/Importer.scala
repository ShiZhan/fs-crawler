/**
 * importer functions
 */
package core

import java.io.IOException
import java.nio.file.{FileSystems, Path, Files, SimpleFileVisitor}
import java.nio.file.FileVisitResult._
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
class ModelFiles extends SimpleFileVisitor[Path] {

  override def visitFile(file: Path, attr: BasicFileAttributes) = {
    println(file.getFileName + " (" + attr.size() + "byte) in " +
            file.getParent.getFileName)
    CONTINUE
  }

  override def postVisitDirectory(dir: Path, e: IOException) = {
    println(dir.getFileName)
    CONTINUE
  }

  override def visitFileFailed(file: Path, e: IOException) = {
    println(e)
    CONTINUE
  }
}

object Importer extends Store(Store.DEFAULT_LOCATION) with Logging {

  def load(name: String) = {
    logger.info("importing RDF/OWL model")
    Files.walkFileTree(FileSystems.getDefault.getPath(name), new ModelFiles)
    close
  }
}
