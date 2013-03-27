/**
 * Apache Jena wrapper
 */
package core

import scala.collection.JavaConverters._

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.tdb.TDBFactory
import com.hp.hpl.jena.query.QueryFactory
import com.hp.hpl.jena.query.QueryExecutionFactory
import com.hp.hpl.jena.query.ReadWrite
import com.hp.hpl.jena.update.GraphStoreFactory
import com.hp.hpl.jena.update.UpdateFactory
import com.hp.hpl.jena.update.UpdateExecutionFactory

/**
 * @author ShiZhan
 * triple store operations
 */
trait Store {

  private val DEFAULT_LOCATION = "data/"
  private val store = TDBFactory.createDataset(DEFAULT_LOCATION)

  def close = store.close()

  def querySelect(sparql: String) = {
    val query = QueryFactory.create(sparql)
    val qexec = QueryExecutionFactory.create(query, store)
    val resultSet = qexec.execSelect
    val solutions = resultSet.asScala.toList
    qexec.close
    solutions
  }

  def queryConstruct(sparql: String) = {
    val query = QueryFactory.create(sparql)
    val qexec = QueryExecutionFactory.create(query, store)
    val resultModel = qexec.execConstruct
    qexec.close
    resultModel
  }

  def queryAsk(sparql: String) = {
    val query = QueryFactory.create(sparql)
    val qexec = QueryExecutionFactory.create(query, store)
    val resultBool = qexec.execAsk
    qexec.close
    resultBool
  }

  def queryUpdate(sparql: String) = {
    val graphStore = GraphStoreFactory.create(store)
    val update = UpdateFactory.create(sparql)
    val updateProcessor = UpdateExecutionFactory.create(update, graphStore)
    store.begin(ReadWrite.WRITE)
    try {
      updateProcessor.execute
      store.commit()
    } finally {
      store.end()
    }
  }

}
