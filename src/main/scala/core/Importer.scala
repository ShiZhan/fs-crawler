/**
 * importer functions
 */
package core

import util.Logging

/**
 * @author ShiZhan
 * 2013
 * import meta-data from model file to TDB data set
 */
object Importer extends Store(Store.DEFAULT_LOCATION) with Logging {

  def load(name: String) = {
    logger.info("importing RDF/OWL model")

    val sparql = "LOAD <%s>".format(name)

    try {
      sparqlUpdateTxn(sparql)
    } catch {
      case e: Exception => println(e)
    }

    logger.info("done")

    close
  }

}
