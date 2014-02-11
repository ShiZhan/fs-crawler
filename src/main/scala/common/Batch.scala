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
    val itemsWithIndex = items.zipWithIndex

    def forAllDo(op: T => Any) = {
      val total = items.size
      val delta = if (total < 100) 1 else total / 100
      println(total + " files found")
      for ((item, i) <- itemsWithIndex) {
        op(item)
        if (i % delta == 0) print("translating [%2d%%]\r".format(i * 100 / total))
      }
      println("translating [100%]")
    }
  }
}