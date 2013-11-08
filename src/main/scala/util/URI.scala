/**
 * Uniform URI generator
 */
package util

import java.io.File
import scalax.file.Path

/**
 * @author ShiZhan
 * Uniform URI generator
 * 1. Generate computer node URI from hostname
 * 2. Generate file URI from File instance with hostname
 * 3. Generate path URI from Path instance with hostname
 */
object URI {
  def fromHost = "file:/" + Platform.hostname
  def fromFile(f: File) = f.toURI.toString.replaceFirst("file:", fromHost)
  def fromPath(p: Path) = p.toAbsolute.toURI.toString.replaceFirst("file:", fromHost)
}