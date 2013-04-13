/**
 * TriGraM translator
 */
package core

import java.io.FileOutputStream
import modeler._
import util.{ Logging, Version }

/**
 * @author ShiZhan
 * translate various resources to semantic model
 */
object Translator extends Logging {

  def createModel(t: String, i: String, o: String) = {
    val modeler = Modelers.getModeler(t).translate _
    val model = modeler(i)
    logger.info("%d triples generated".format(model.size))
    if (!model.isEmpty) {
      model.write(new FileOutputStream(o), "RDF/XML-ABBREV")
      logger.info("model saved to file [%s]".format(o))
    } else {
      logger.info("model is empty")
    }
  }

  def createCoreModel = {
    val coreModel = Modelers.getCoreModel
    coreModel.write(new FileOutputStream(TGM.local), "RDF/XML-ABBREV")
    logger.info("[%d] triples saved to core model file [%s]".
      format(coreModel.size, TGM.local))
  }

  val help = Modelers.getModelerHelp

}