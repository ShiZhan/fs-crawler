/**
 * model functions
 */
package model

import java.io._
import concurrent.Future
import jena._
import com.hp.hpl.jena.sparql.core._
import com.hp.hpl.jena.tdb.{ TDB, TDBFactory }
import org.w3.banana._
import org.w3.banana.jena.JenaOperations._

import util.Logging

/**
 * @author ShiZhan
 * 2013
 * Model object
 */
object Model extends Logging {

  def importFromRoot(rootDirName: String) = {
    val input = new File(rootDirName)
    val rootDir = if (input.isDirectory()) input else new File(input.getParent())
    val absolutePathOfRoot = rootDir.getAbsolutePath()

    logger.info("initializing model with root directory: " + absolutePathOfRoot)

    println(rootDir.list().mkString("\n"))
  }

  def queryStore(q: String): String = {
    return "work in progess"
  }

}