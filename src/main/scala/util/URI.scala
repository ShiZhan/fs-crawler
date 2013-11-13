/**
 * URI generator
 */
package util

import java.io.File
import scalax.file.Path

/**
 * @author ShiZhan
 * URI generator
 * 1. Generate computer node URI from hostname
 * 2. Generate file URI from File instance with hostname
 * 3. Generate path URI from Path instance with hostname
 * 4. Generate URI from path String with hostname
 * another set: use dedicated scheme
 * NOTE: URIBuilder will escape UTF-8 characters, but File/Path API won't.
 */
object URI {
  val scheme = "trigram:"

  def fromHost = scheme + '/' + Platform.HOSTNAME
  def fromFile(f: File) = f.toURI.toString.replaceFirst("file:", fromHost)
  def fromPath(p: Path) = p.toURI.toString.replaceFirst("file:", fromHost)

  def pathString2URI(path: String) = {
    val rootUNC = if (path.head.isLetter) ('/' + path) else path
    val posix = rootUNC.replace('\\', '/')
    val f = new File(posix)
    val trimming = if (Platform.isWindows) 8 else 5
    f.toURI.toString.substring(trimming)
  }

  def fromString(str: String) = fromHost + pathString2URI(str)

  def fromHost(s: String) = s + '/' + Platform.HOSTNAME
  def fromFile(s: String, f: File) = f.toURI.toString.replaceFirst("file:", fromHost(s))
  def fromPath(s: String, p: Path) = p.toURI.toString.replaceFirst("file:", fromHost(s))

  def fromString(s: String, str: String) = fromHost(s) + pathString2URI(str)
}