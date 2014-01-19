/**
 * API for getting build-in resources
 */
package helper

/**
 * @author ShiZhan
 * API for getting build-in resources
 */
object BuildIn {
  def get(name: String) = getClass.getClassLoader.getResourceAsStream(name)
  def getString(name: String) = {
    try {
      val is = get(name)
      io.Source.fromInputStream(is).mkString
    } catch {
      case e: Exception => null
    }
  }
}