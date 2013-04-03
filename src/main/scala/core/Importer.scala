/**
 * importer functions
 */
package core

import java.io.File
import java.nio.file.Files
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

  private def traverseDirectory(d: File): Array[File] = {
    val all = d.listFiles
    for (i <- all) {
      println(i.getName + " (in) " + i.getParent)
      val a = Files.readAttributes(i.toPath, classOf[BasicFileAttributes])
      println(a.creationTime + "|" + a.lastAccessTime + "|" + a.lastModifiedTime)
    }
    all ++ all.filter(_.isDirectory).flatMap(traverseDirectory)
  }

  private def readDirectory(d: File): Model = {
    logger.info("initializing model with root directory: " + d.getAbsolutePath)

    val m = ModelFactory.createDefaultModel
    val dirTree = traverseDirectory(d)

    logger.info("object total: " + dirTree.length)

    m
  }

  private def readFile(f: File): Model = {
    val m = ModelFactory.createDefaultModel
    FileManager.get.readModel(m, f.getName)
    m
  }

  private def readUnknown(f: File): Model = {
    logger.info("unrecognized reource: " + f.getName)
    ModelFactory.createDefaultModel
  }

  def readData(name: String): Model = {
    val input = new File(name)
    val parser = if (input.isDirectory) readDirectory(_)
    else if (input.isFile) readFile(_) else readUnknown(_)
    parser(input)
  }

  def load(name: String): Unit = {
    logger.info("importing RDF/OWL model")
    readData(name)
    close
    return
  }
}
