/**
 * Gauge operations with console feedback
 */
package common

/**
 * @author ShiZhan
 * Sequential operations with console feedback
 * E.g.: to process a large amount of files
 */
object Gauge {
  implicit class ArrayOperations[T](items: Array[T]) {
    def foreachDo(op: T => Any) = {
      var i = 0
      val total = items.length
      val delta = if (total < 100) 1 else total / 100
      println(total + " objects to process")
      for (item <- items) {
        op(item)
        if (i % delta == 0) print("processing [%2d%%]\r".format(i * 100 / total))
        i += 1
      }
      println("processing [100%]")
    }
  }
}