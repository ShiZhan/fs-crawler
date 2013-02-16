/**
 *
 */
import java.io._
import jena._
import com.hp.hpl.jena.sparql.core._
import com.hp.hpl.jena.tdb.{TDB, TDBFactory}
/**
 * @author ShiZhan
 * 2013
 * model functions
 */
package object model {

	def importModel(rootDir: String) = {
    val absolutePathOfRoot = (new File(rootDir)).getAbsolutePath()

    println("initializing model with root directory: " + absolutePathOfRoot)
	}
}
