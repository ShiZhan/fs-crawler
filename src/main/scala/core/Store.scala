/**
 * Apache Jena wrapper
 */
package core

import scala.collection.JavaConverters._

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.tdb.TDBFactory
import com.hp.hpl.jena.query.{ QueryFactory, QueryExecutionFactory, ReadWrite }
import com.hp.hpl.jena.update.{
  GraphStoreFactory,
  UpdateAction,
  UpdateFactory,
  UpdateExecutionFactory
}

/**
 * @author ShiZhan
 * triple store operations
 */
class Store(val location: String) {

  private val store = TDBFactory.createDataset(location)

  def close = store.close()

  def querySelect(sparql: String) = {
    val query = QueryFactory.create(sparql)
    val qExec = QueryExecutionFactory.create(query, store)
    val resultSet = qExec.execSelect
    val solutions = resultSet.asScala.toList
    qExec.close
    solutions
  }

  def queryConstruct(sparql: String) = {
    val query = QueryFactory.create(sparql)
    val qExec = QueryExecutionFactory.create(query, store)
    val resultModel = qExec.execConstruct
    qExec.close
    resultModel
  }

  def queryAsk(sparql: String) = {
    val query = QueryFactory.create(sparql)
    val qExec = QueryExecutionFactory.create(query, store)
    val resultBool = qExec.execAsk
    qExec.close
    resultBool
  }

  def queryDescribe(sparql: String) = {
    val query = QueryFactory.create(sparql)
    val qExec = QueryExecutionFactory.create(query, store)
    val resultModel = qExec.execDescribe
    qExec.close
    resultModel
  }

  def sparqlQuery(sparql: String): Any = {
    val query = QueryFactory.create(sparql)
    val qExec = QueryExecutionFactory.create(query, store)
    val result = query.getQueryType match {
      case 111 => qExec.execSelect.asScala.toList
      case 222 => qExec.execConstruct
      case 333 => qExec.execDescribe
      case 444 => qExec.execAsk
      case _ => "unkown query"
    }
    qExec.close
    result
  }

  def sparqlUpdate(sparql: String) = {
    val graphStore = GraphStoreFactory.create(store)
    UpdateAction.parseExecute(sparql, graphStore)
  }

  def sparqlUpdateTxn(sparql: String) = {
    store.begin(ReadWrite.WRITE)
    try {
      val update = UpdateFactory.create(sparql)
      val graphStore = GraphStoreFactory.create(store)
      val uExec = UpdateExecutionFactory.create(update, graphStore)
      uExec.execute
      store.commit
    } finally {
      store.end
    }
  }

}

object Store {
  val DEFAULT_LOCATION = "data/"
}
