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
 * Model Factory object
 * use different classes to support various data sources
 */
object Store extends Logging {

  val DEFAULT_LOCATION = "data/"
  def initDataset = TDBFactory.createDataset(DEFAULT_LOCATION)
  // close dataset with dataset.close()

  def queryDataset(m: Dataset, q: String): String = {
    //    val qexec = QueryExecutionFactory.create(QueryFactory.create(q), m)
    //    val results = qexec.execSelect
    //    qexec.close
    //    results.toString
    "work in progress: " + q
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
    logger.info("importing RDF/OWL model")
    val m = ModelFactory.createDefaultModel
    FileManager.get.readModel( m, f.getName )
    println(m.size)
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
    val model = initDataset
    model.addNamedModel(name, parseData(name: String))
    model.close
  }
}
