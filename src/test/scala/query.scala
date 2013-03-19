import scala.io.Source
import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.query.Dataset
import com.hp.hpl.jena.query.Query
import com.hp.hpl.jena.query.QueryExecution
import com.hp.hpl.jena.query.QueryExecutionFactory
import com.hp.hpl.jena.query.QueryFactory
import com.hp.hpl.jena.query.ResultSet
import com.hp.hpl.jena.query.ResultSetFormatter
import com.hp.hpl.jena.tdb.TDBFactory

object QuerySelect {
  def main(args: Array[String]) =
    if(args.length < 2)
      println("run with <dataset> <query>")
    else {
      val store = TDBFactory.createDataset(args(0))
      val queryString = Source.fromFile(args(1)).getLines.mkString("\n")
      println("Query:\n" + queryString)
      val query = QueryFactory.create(queryString)
      val qexec = QueryExecutionFactory.create(query, store)
      val results = qexec.execSelect
      ResultSetFormatter.out(results)
      qexec.close
      store.close
    }
}

object QueryConstruct {
  def main(args: Array[String]) =
    if(args.length < 2)
      println("run with <dataset> <query>")
    else {
      val store = TDBFactory.createDataset(args(0))
      val queryString = Source.fromFile(args(1)).getLines.mkString("\n")
      println("Query:\n" + queryString)
      val query = QueryFactory.create(queryString)
      val qexec = QueryExecutionFactory.create(query, store)
      val results = qexec.execConstruct
      println(results.size)
      qexec.close
      store.close
    }
}
