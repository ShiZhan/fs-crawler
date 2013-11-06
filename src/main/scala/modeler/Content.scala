/**
 * Modeler to translate directory content into (data, relation) model
 */
package modeler

import util.{ Logging, Version, DateTime, Hash }

import modeler.{ CimVocabulary => CIM }

/**
 * @author ShiZhan
 * Translate actual content of 'virtually' any data source into
 * structural model (checksum, size, link-to-origin) for easy comparison
 * the resulting model will be organized in two folds:
 * 1. k-v pair stands for
 */
object Content extends Modeler with Logging {
  override val key = "con"

  override val usage = "Translate directory content"

  def run(input: String, output: String) = {}
}