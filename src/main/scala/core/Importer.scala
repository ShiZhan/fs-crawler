/**
 * importer functions
 */
package core

/**
 * @author ShiZhan
 * 2013
 * import meta-data from model file to TDB data set
 */
//object Importer extends Store(Store.defaultLocation) with Logging {
object Importer {

  def load(name: String) = tdb.tdbloader.main("--loc=" + Store.defaultLocation, name)

}