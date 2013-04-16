/**
 * Modeler to translate directory
 */
package modeler

import scalax.file.{ Path, PathSet }
import com.hp.hpl.jena.rdf.model.{ ModelFactory, Model }
import util.{ Logging, Version, DateTime, Hash }

/**
 * @author ShiZhan
 * translate HUGE directory structure into TriGraM model
 * using string interpolation
 */
object DirectoryEx extends Modeler with Logging {

  private val license = """
Copyright 2013 Shi.Zhan.
Licensed under the Apache License, Version 2.0 (the &quot;License&quot;);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing
permissions and limitations under the License. 
"""

  def usage = "Translate *HUGE* directory structure into TriGraM model"

  def core = {
    logger.info("initialize core model")

    ModelFactory.createDefaultModel
  }

  def translate(i: String, o: String) = {
    val p = Path(i)

    logger.info("create model for *HUGE* directory")
  }

}