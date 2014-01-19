/**
 * API for getting build-in resources
 */
package helper

/**
 * @author ShiZhan
 * API for getting build-in resources
 */
object BuildIn extends Logging {
  def get(name: String) = getClass.getClassLoader.getResourceAsStream(name)

  def getString(name: String) = {
    try {
      val is = get(name)
      io.Source.fromInputStream(is).mkString
    } catch {
      case e: Exception => logger.error(e.toString); null
    }
  }

  def getStringOrElse(name: String, default: String) = {
    try {
      val is = get(name)
      io.Source.fromInputStream(is).mkString
    } catch {
      case e: Exception => logger.error(e.toString); default
    }
  }
}