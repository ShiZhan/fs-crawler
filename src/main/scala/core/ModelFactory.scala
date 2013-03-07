/**
 * model functions
 */
package core

import java.io._

import com.hp.hpl.jena.query.Dataset ;
import com.hp.hpl.jena.query.Query ;
import com.hp.hpl.jena.query.QueryExecution ;
import com.hp.hpl.jena.query.QueryExecutionFactory ;
import com.hp.hpl.jena.query.QueryFactory ;
import com.hp.hpl.jena.query.ResultSet ;
import com.hp.hpl.jena.query.ResultSetFormatter ;
import com.hp.hpl.jena.tdb.TDBFactory ;

import util.Logging

/**
 * @author ShiZhan
 * 2013
 * Model Factory object
 * use different classes to support various data sources
 */
object ModelFactory extends Logging {

  val DEFAULT_LOCATION = "data/"
  val dataSet = TDBFactory.createDataset(DEFAULT_LOCATION)

  def shutdown = dataSet.close

  def queryStore(q: String): String = {
    val sparqlQueryString = "SELECT (count(*) AS ?count) { ?s ?p ?o }"
    val query = QueryFactory.create(sparqlQueryString)
    val qexec = QueryExecutionFactory.create(query, dataSet)
    val results = qexec.execSelect
    qexec.close()
    results.toString
  }

  def recursiveListFiles(f: File): Array[File] = {
    val all = f.listFiles
    all ++ all.filter(_.isDirectory).flatMap(recursiveListFiles)
  }

  def importDirToModel(rootDirName: String) = {
    val input = new File(rootDirName)
    val rootDir = if (input.isDirectory) input else new File(input.getParent)
    val absolutePathOfRoot = rootDir.getAbsolutePath

    logger.info("initializing model with root directory: " + absolutePathOfRoot)

    val dirTree = recursiveListFiles(rootDir)
    dirTree.foreach(item => println(item.getName + " (in) " + item.getParent))
    
    println("object total: " + dirTree.length)
  }

}