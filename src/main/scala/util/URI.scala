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
  def fromHost = "file:/" + Platform.hostname
  def fromFile(f: File) = f.toURI.toString.replaceFirst("file:", fromHost)
  def fromPath(p: Path) = p.toAbsolute.toURI.toString.replaceFirst("file:", fromHost)
  def fromString(s: String) = fromHost + '/' + URLEncoder.encode(s)
}