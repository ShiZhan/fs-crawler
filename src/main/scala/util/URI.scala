/**
 * URI generator
 */
package util

import java.io.File
import scalax.file.Path
import org.apache.http.client.utils.URIBuilder

/**
 * @author ShiZhan
 * URI generator
 * 1. Generate computer node URI from hostname
 * 2. Generate file URI from File instance with hostname
 * 3. Generate path URI from Path instance with hostname
 * 4. Generate URI from plain String with hostname
 * 5. Generate URI from path-like String with hostname
 * another set: use dedicated prefix
 */
object URI {
  val prefix = "trigram:/"

  def fromHost = prefix + Platform.hostname + '/'

  def fromFile(file: File) =
    file.toURI.toString.replaceFirst("file:/", fromHost)

  def fromPath(path: Path) =
    path.toAbsolute.toURI.toString.replaceFirst("file:/", fromHost)

  val ub = new URIBuilder
  def pathEscape = ub.setPath(_)
  def fromString(s: String) = fromHost + pathEscape(s)

  def fromHost(p: String) = p + Platform.hostname + '/'

  def fromFile(p: String, file: File) =
    file.toURI.toString.replaceFirst("file:/", fromHost(p))

  def fromPath(p: String, path: Path) =
    path.toAbsolute.toURI.toString.replaceFirst("file:/", fromHost(p))

  def fromString(p: String, s: String) = fromHost(p) + pathEscape(s)
}