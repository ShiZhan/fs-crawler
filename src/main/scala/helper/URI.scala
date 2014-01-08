/**
 * URI generator
 */
package helper

/**
 * @author ShiZhan
 * URI generator
 * 1. Generate computer node URI from hostname
 * 2. Generate file URI from File instance with hostname
 * 3. Generate URI from path String with hostname
 * another set: use dedicated scheme
 */
object URI {
  import java.io.File

  def pathString2URI(path: String) = {
    val rootUNC = if (path.head.isLetter) ('/' + path) else path
    val posix = rootUNC.replace('\\', '/')
    val f = new File(posix)
    val trim = if (Platform.isWindows && path.head == '/') 8 else 5
    f.toURI.toString.substring(trim)
  }

  val scheme = "trigram:"

  def fromHost = scheme + '/' + Platform.HOSTNAME
  def fromFile(f: File) = fromHost + f.toURI.getRawPath
  def fromString(str: String) = fromHost + pathString2URI(str)

  def fromHost(s: String) = s + '/' + Platform.HOSTNAME
  def fromFile(s: String, f: File) = fromHost(s) + f.toURI.getRawPath
  def fromString(s: String, str: String) = fromHost(s) + pathString2URI(str)
}