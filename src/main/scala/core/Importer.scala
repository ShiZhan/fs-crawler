/**
 * importer functions
 */
package core

import java.io._

import com.hp.hpl.jena.rdf.model._

import com.hp.hpl.jena.vocabulary.RDF
import com.hp.hpl.jena.vocabulary.RDFS

import com.hp.hpl.jena.util.FileManager

import util.Logging

/**
 * @author ShiZhan
 * 2013
 * import metadata from various data sources
 */
object Importer extends Store with Logging {

  private def traverseDirectory(d: File): Array[File] = {
    val all = d.listFiles
    all.foreach(item => println(item.getName + " (in) " + item.getParent))
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
