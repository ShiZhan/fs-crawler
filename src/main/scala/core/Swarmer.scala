/**
 * TriGraM Scales out
 */
package core

import util.Logging

/**
 * @author ShiZhan
 * collaborate with other TriGraM nodes as model implies
 * 1. query remote TDB if local triple refer to remote URI
 * 2. start TDB server over HTTP (maybe Jena Fuseki)
 */
object Swarmer extends Logging {

  logger.info("TODO: make storage query and update distributely")

}