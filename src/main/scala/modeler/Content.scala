/**
 * Modeler to translate directory content into (data, relation) model
 */
package modeler

import java.io.{ File, FileOutputStream }
import scalax.file.Path
import util.{ Logging, Version, DateTime, Hash }

import modeler.{ CimVocabulary => CIM }

/**
 * @author ShiZhan
 * Translate content characters of 'virtually' any data source into
 * structural model item(checksum, size, path, [item, ...]) for easy comparison
 * the resulting model will be organized in two folds:
 * 1. k-v pair stands for
 */
object Content extends Modeler with Logging {
  override val key = "con"

  override val usage = "[directory] into [structural checksum group]"

  def run(input: String, output: String) = {}
}