package core

trait TOperation

case class Query(q: String) extends TOperation
case class QuitOp(reason: String) extends TOperation

trait TResult

case class QueryResult(r: String) extends TResult
//case class QuitConfirm extends TResult
