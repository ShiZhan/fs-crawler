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
    val store = TDBFactory.createDataset(DEFAULT_LOCATION)
    val query = "SELECT * {?s ?p ?o}"
    val qexec = QueryExecutionFactory.create(QueryFactory.create(query), store)
    val results = qexec.execSelect
    qexec.close
    store.close
    println(results.toString)
  }
}
