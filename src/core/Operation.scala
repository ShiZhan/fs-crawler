package core

trait TOperation

case class Query(queryString: String) extends TOperation

trait TResult

case class QueryResult(resultString: String) extends TResult
