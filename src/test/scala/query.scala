import scala.io.Source

import scala.collection.JavaConverters._

import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.query.Dataset
import com.hp.hpl.jena.query.Query
import com.hp.hpl.jena.query.QueryExecution
import com.hp.hpl.jena.query.QueryExecutionFactory
import com.hp.hpl.jena.query.QueryFactory
import com.hp.hpl.jena.query.ResultSet
import com.hp.hpl.jena.query.ResultSetFormatter
import com.hp.hpl.jena.tdb.TDBFactory

import com.hp.hpl.jena.query.ReadWrite
import com.hp.hpl.jena.update.GraphStore
import com.hp.hpl.jena.update.GraphStoreFactory
import com.hp.hpl.jena.update.UpdateExecutionFactory
import com.hp.hpl.jena.update.UpdateFactory
import com.hp.hpl.jena.update.UpdateProcessor
import com.hp.hpl.jena.update.UpdateRequest

object SparqlQuery {
  def main(args: Array[String]) =
    if (args.length < 2)
      println("run with <dataset> <query>")
    else {
      val store = TDBFactory.createDataset(args(0))
      val sparqlString = Source.fromFile(args(1)).getLines.mkString("\n")
      println("SPARQL:\n" + sparqlString)

      // recognize query type
      val query = QueryFactory.create(sparqlString)
      val qexec = QueryExecutionFactory.create(query, store)
      val resultSet = qexec.execSelect
      val solutions = resultSet.asScala.toIterable
      qexec.close
      store.close
      println(solutions.toList)
    }
}

object QuerySelect {
  def main(args: Array[String]) =
    if (args.length < 2)
      println("run with <dataset> <select query>")
    else {
      val store = TDBFactory.createDataset(args(0))
      val sparqlString = Source.fromFile(args(1)).getLines.mkString("\n")
      println("SPARQL:\n" + sparqlString)
      val query = QueryFactory.create(sparqlString)
      val qexec = QueryExecutionFactory.create(query, store)
      val resultSet = qexec.execSelect
      val solutions = resultSet.asScala.toIterable
      qexec.close
      store.close
      println(solutions.toList)
    }
}

object QueryConstruct {
  def main(args: Array[String]) =
    if (args.length < 2)
      println("run with <dataset> <construct query>")
    else {
      val store = TDBFactory.createDataset(args(0))
      val sparqlString = Source.fromFile(args(1)).getLines.mkString("\n")
      println("SPARQL:\n" + sparqlString)
      val query = QueryFactory.create(sparqlString)
      val qexec = QueryExecutionFactory.create(query, store)
      val resultModel = qexec.execConstruct
      qexec.close
      store.close
      println(resultModel)
    }
}

object QueryAsk {
  def main(args: Array[String]) =
    if (args.length < 2)
      println("run with <dataset> <ask query>")
    else {
      val store = TDBFactory.createDataset(args(0))
      val sparqlString = Source.fromFile(args(1)).getLines.mkString("\n")
      println("SPARQL:\n" + sparqlString)
      val query = QueryFactory.create(sparqlString)
      val qexec = QueryExecutionFactory.create(query, store)
      val results = qexec.execAsk
      println(results)
      qexec.close
      store.close
    }
}

object QueryDescribe {
  def main(args: Array[String]) =
    if (args.length < 2)
      println("run with <dataset> <describe query>")
    else {
      val store = TDBFactory.createDataset(args(0))
      val sparqlString = Source.fromFile(args(1)).getLines.mkString("\n")
      println("SPARQL:\n" + sparqlString)
      val query = QueryFactory.create(sparqlString)
      val qexec = QueryExecutionFactory.create(query, store)
      val results = qexec.execDescribe
      println(results)
      qexec.close
      store.close
    }
}

object QueryUpdate {
  def main(args: Array[String]) =
    if (args.length < 2)
      println("run with <dataset> <update query>")
    else {
      val store = TDBFactory.createDataset(args(0))
      val sparqlString = Source.fromFile(args(1)).getLines.mkString("\n")
      println("SPARQL:\n" + sparqlString)
      val graphStore = GraphStoreFactory.create(store)
      val update = UpdateFactory.create(sparqlString)
      val updateProcessor = UpdateExecutionFactory.create(update, graphStore)
      store.begin(ReadWrite.WRITE)
      try {
        updateProcessor.execute
        store.commit()
        // Or call .abort()
      } finally {
        store.end()
      }
    }
}
