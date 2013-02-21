/**
 *
 */
package model

import java.io._
import jena._
import com.hp.hpl.jena.sparql.core._
import com.hp.hpl.jena.tdb.{ TDB, TDBFactory }

import util.Logging

/**
 * @author ShiZhan
 * 2013
 * model functions
 */
object Model extends Logging {

  def importFromRoot(rootDir: String) = {
    val absolutePathOfRoot = (new File(rootDir)).getAbsolutePath()

    logger.info("initializing model with root directory: " + absolutePathOfRoot)

  }
  
  def queryStore(q: String): String = {
    return "work in progess"
  }

}