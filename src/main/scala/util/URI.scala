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
 * 4. Generate URI from path String with hostname
 * another set: use dedicated prefix
 */
object URI {
  val scheme = "trigram:"

  def fromHost = scheme + '/' + Platform.HOSTNAME
  def fromFile(f: File) = f.toURI.toString.replaceFirst("file:", fromHost)
  def fromPath(p: Path) = p.toURI.toString.replaceFirst("file:", fromHost)

  private val ub = new URIBuilder
  private def pathEscape(s: String) = {
    val inCaseUncPathinLead = s.replace('\\', '/')
    val pathURI = ub.setPath(inCaseUncPathinLead).toString
    if (pathURI.head == '/') pathURI else '/' + pathURI
  }

  def fromString(str: String) = fromHost + pathEscape(str)

  def fromHost(s: String) = s + '/' + Platform.HOSTNAME
  def fromFile(s: String, f: File) = f.toURI.toString.replaceFirst("file:", fromHost(s))
  def fromPath(s: String, p: Path) = p.toURI.toString.replaceFirst("file:", fromHost(s))

  def fromString(s: String, str: String) = fromHost(s) + pathEscape(str)
}