/**
 *
 */
package server

import util.Logging

/**
 * @author ShiZhan
 *
 */
object Server extends Logging {

  def run(address: String) {
    logger.info("Starting server on " + address)
  }

}