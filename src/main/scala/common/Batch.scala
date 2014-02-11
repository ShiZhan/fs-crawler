/**
 * Batch operations with console feedback
 */
package common

/**
 * @author ShiZhan
 * Batch operations with console feedback
 * E.g.: to process a large amount of files
 */
object Batch {
  implicit class ArrayOperations[T](items: Array[T]) {
    def forAllDo(op: T => Any) = {
      var i = 0
      val total = items.size
      val delta = if (total < 100) 1 else total / 100
      println(total + " objects found")
      for (item <- items) {
        op(item)
        if (i % delta == 0) print("processing [%2d%%]\r".format(i * 100 / total))
        i += 1
      }
      println("processing [100%]")
    }
  }
}