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

object QueryStore {
  def main(args: Array[String]) = {
    val DEFAULT_LOCATION = "data/"
    val SPARQL_FILE = "sparql/list.sparql"
    val store = TDBFactory.createDataset(DEFAULT_LOCATION)
    val queryString = Source.fromFile(SPARQL_FILE).getLines.mkString("\n")
    println("Query:\n" + queryString)
    val query = QueryFactory.create(queryString)
    val qexec = QueryExecutionFactory.create(query, store)
    val results = qexec.execSelect
    ResultSetFormatter.out(results)
    qexec.close
    store.close
  }
}
