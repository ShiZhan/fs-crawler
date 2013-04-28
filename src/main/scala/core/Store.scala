/**
 * Apache Jena TDB wrapper class
 */
package core

import scala.collection.JavaConversions._
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.tdb.TDBFactory
import com.hp.hpl.jena.query.{
  QueryFactory,
  QueryExecutionFactory,
  ReadWrite
}
import com.hp.hpl.jena.query.Query.{
  QueryTypeSelect,
  QueryTypeConstruct,
  QueryTypeDescribe,
  QueryTypeAsk
}
import com.hp.hpl.jena.update.{
  GraphStoreFactory,
  UpdateAction,
  UpdateFactory,
  UpdateExecutionFactory
}

/**
 * @author ShiZhan
 * triple store operations
 * wrapper of Apache Jena TDB, each instance stands for a TDB assembly
 */
class Store(val location: String) {

  private val store = TDBFactory.createDataset(location)

  def close = store.close()

  def querySelect(sparql: String) = {
    val query = QueryFactory.create(sparql)
    val qExec = QueryExecutionFactory.create(query, store)
    val resultList = qExec.execSelect.toList
    qExec.close
    resultList
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
      case QueryTypeSelect => qExec.execSelect.toList
      case QueryTypeConstruct => qExec.execConstruct
      case QueryTypeDescribe => qExec.execDescribe
      case QueryTypeAsk => qExec.execAsk
      case _ => null
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
  val defaultLocation = "data/"
  def apply() = new Store(defaultLocation)
  def apply(loc: String) = new Store(loc)
}