/**
 * URI generator
 */
package util

import java.io.File
import java.net.URLEncoder
import scalax.file.Path

/**
 * @author ShiZhan
 * URI generator
 * 1. Generate computer node URI from hostname
 * 2. Generate file URI from File instance with hostname
 * 3. Generate path URI from Path instance with hostname
 * 4. Generate URI from plain String with hostname
 */
object URI {
  val prefix = "file:/"
  def fromHost = prefix + Platform.hostname + '/'
  def fromFile(file: File) = file.toURI.toString.replaceFirst("file:/", fromHost)
  def fromPath(path: Path) = path.toAbsolute.toURI.toString.replaceFirst("file:/", fromHost)
  def fromString(s: String) = fromHost + '/' + URLEncoder.encode(s)

  def fromHost(p: String) = p + Platform.hostname + '/'
  def fromFile(p: String, file: File) =
    file.toURI.toString.replaceFirst("file:/", fromHost(p))
  def fromPath(p: String, path: Path) =
    path.toAbsolute.toURI.toString.replaceFirst("file:/", fromHost(p))
  def fromString(p: String, s: String) = fromHost(p) + '/' + URLEncoder.encode(s)
}