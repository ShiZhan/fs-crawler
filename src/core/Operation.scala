package core

trait TOperation

case class Query(q: String) extends TOperation

trait TResult

case class QueryResult(r: String) extends TResult
