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

  def getString(name: String) = io.Source.fromInputStream(get(name)).mkString

  def getStringOrElse(name: String, default: String) =
    try { getString(name) }
    catch { case e: Exception => logger.error(e.toString); default }
}