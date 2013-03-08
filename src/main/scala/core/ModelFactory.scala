/**
 * model functions
 */
package core

import java.io._

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.query.Dataset
import com.hp.hpl.jena.query.Query
import com.hp.hpl.jena.query.QueryExecution
import com.hp.hpl.jena.query.QueryExecutionFactory
import com.hp.hpl.jena.query.QueryFactory
import com.hp.hpl.jena.query.ResultSet
import com.hp.hpl.jena.query.ResultSetFormatter
import com.hp.hpl.jena.tdb.TDBFactory

import util.Logging

/**
 * @author ShiZhan
 * 2013
 * Model Factory object
 * use different classes to support various data sources
 */
object ModelFactory extends Logging {

  val DEFAULT_LOCATION = "data/"
  def loadStore = TDBFactory.createDataset(DEFAULT_LOCATION)
  // close dataset with dataset.close()

  def queryStore(m: Dataset, q: String): String = {
    //    val qexec = QueryExecutionFactory.create(QueryFactory.create(q), m)
    //    val results = qexec.execSelect
    //    qexec.close
    //    results.toString
    "work in progress: " + q
  }

  def posixInterpreter(cmd: String) = cmd

  def unknownInterpreter(cmd: String) = "unsupported command"

  def traverseDirectory(d: File): Array[File] = {
    val all = d.listFiles
    all.foreach(item => println(item.getName + " (in) " + item.getParent))
    all ++ all.filter(_.isDirectory).flatMap(traverseDirectory)
  }

  def parseDirectory(d: File) = {
    logger.info("initializing model with root directory: " + d.getAbsolutePath)

    val dirTree = traverseDirectory(d)

    logger.info("object total: " + dirTree.length)

    //    translating directory data to model
  }

  def parseFile(f: File) = {
    logger.info("not implemented")

    //    check file format and decide which dedicated parser to use
  }

  def parseUnknown(f: File) = {
    logger.info("unrecognized file reource")

    //    check file format and decide which dedicated parser to use
  }

  def importData(name: String) = {
    val input = new File(name)
    val parser = if (input.isDirectory) parseDirectory(_)
    else if (input.isFile) parseFile(_) else parseUnknown(_)
    parser(input)
  }

}