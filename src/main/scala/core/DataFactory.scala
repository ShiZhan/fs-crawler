/**
 * model functions
 */
package core

import java.io._

import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.query.Dataset
import com.hp.hpl.jena.query.Query
import com.hp.hpl.jena.query.QueryExecution
import com.hp.hpl.jena.query.QueryExecutionFactory
import com.hp.hpl.jena.query.QueryFactory
import com.hp.hpl.jena.query.ResultSet
import com.hp.hpl.jena.query.ResultSetFormatter
import com.hp.hpl.jena.tdb.TDBFactory
import com.hp.hpl.jena.util.FileManager
import com.hp.hpl.jena.vocabulary.RDF
import com.hp.hpl.jena.vocabulary.RDFS

import util.Logging

/**
 * @author ShiZhan
 * 2013
 * DataFactory
 * manipulate various data sources
 */
object DataFactory extends Logging {

  private val DEFAULT_LOCATION = "data/"

  private val store = TDBFactory.createDataset(DEFAULT_LOCATION)
  // close dataset with store.close()

  def queryDataset(q: String): String = {
    val query = "SELECT * {?s ?p ?o}"
    val qexec = QueryExecutionFactory.create(QueryFactory.create(query), store)
    val results = qexec.execSelect
    qexec.close
    results.toString
//    "work in progress: " + q
  }

  def interpreterPosix(cmd: String) = cmd

  def interpreterUnknown(cmd: String) = "unsupported command"

  private def traverseDirectory(d: File): Array[File] = {
    val all = d.listFiles
    all.foreach(item => println(item.getName + " (in) " + item.getParent))
    all ++ all.filter(_.isDirectory).flatMap(traverseDirectory)
  }

  private def parseDirectory(d: File): Model = {
    logger.info("initializing model with root directory: " + d.getAbsolutePath)

    val m = ModelFactory.createDefaultModel
    val dirTree = traverseDirectory(d)

    logger.info("object total: " + dirTree.length)

    m
  }

  private def parseFile(f: File): Model = {
    val m = ModelFactory.createDefaultModel
    FileManager.get.readModel(m, f.getName)
    m
  }

  private def parseUnknown(f: File): Model = {
    logger.info("unrecognized reource: " + f.getName)
    ModelFactory.createDefaultModel
  }

  def parseData(name: String): Model = {
    val input = new File(name)
    val parser = if (input.isDirectory) parseDirectory(_)
    else if (input.isFile) parseFile(_) else parseUnknown(_)
    parser(input)
  }

  def load(name: String) = {
    logger.info("importing RDF/OWL model")
    store.addNamedModel(name, parseData(name))
  }
}
