/**
 *
 */
/**
 * @author ShiZhan
 *
 */
import scala.xml.XML

object CountNodes {

  def main(args: Array[String]) =
    if (args.length < 1)
      println("run with <XML file>")
    else {
      val i = XML.loadFile(args(0))
      val classes = i \\ "VALUE.OBJECT" \ "CLASS"
      val references = classes.flatMap(c => c \ "PROPERTY.REFERENCE")
      val properties = classes.flatMap(c => c \ "PROPERTY" ++ c \ "PROPERTY.ARRAY")
      val objProp = (Seq[String]() /: references) { (r, c) =>
        val n = (c \ "@NAME").toString
        if (r.contains(n)) r else r ++ Seq(n)
      }
      val datProp = (Seq[String]() /: properties) { (r, c) =>
        val n = (c \ "@NAME").toString
        if (r.contains(n)) r else r ++ Seq(n)
      }
      println("[%d]->[%d] [%d]->[%d]".
        format(references.length, objProp.length, properties.length, datProp.length))
    }

}

object TravelClasses {

  def main(args: Array[String]) =
    if (args.length < 1)
      println("run with <XML file>")
    else {
      val i = XML.loadFile(args(0))
      val classes = i \\ "VALUE.OBJECT" \ "CLASS"
      for (c <- classes) {
        val cName = (c \ "@NAME").toString
        val cSuperName = (c \ "@SUPERCLASS").toString
        val cQualifier = c \ "QUALIFIER"
        val cIsAsso = cQualifier.map(q => (q \ "@NAME").toString).contains("Association")
        if (cSuperName.isEmpty)
          println(cName + " : " + cIsAsso + " has no super class")
        else
          println(cName + " : " + cIsAsso + "|" + cSuperName)
      }
    }

}

// use this code to generate vocabulary used by modelers
// need to update with the DMTF CIM version
object GetVocabulary {

  def main(args: Array[String]) =
    if (args.length < 1)
      println("run with <DMTF CIM Schema XML file>")
    else {
      val i = XML.loadFile(args(0))
      val cNodes = i \\ "CIM" \ "DECLARATION" \ "DECLGROUP" \ "VALUE.OBJECT" \ "CLASS"
      val rNodes = cNodes.flatMap(c => c \ "PROPERTY.REFERENCE")
      val pNodes = cNodes.flatMap(c => c \ "PROPERTY" ++ c \ "PROPERTY.ARRAY")
      val rNames = rNodes.map(_ \ "@NAME" text) distinct
      val pNames = pNodes.map(_ \ "@NAME" text) distinct
      val cNames = cNodes.map(_ \ "@NAME" text)
      val cFile = new java.io.File("CIM-CLASS")
      val pFile = new java.io.File("CIM-PROPERTY")
      val cFileStream = new java.io.PrintStream(cFile)
      val pFileStream = new java.io.PrintStream(pFile)
      cNames.foreach(cFileStream.println)
      rNames.foreach(pFileStream.println)
      pNames.foreach(pFileStream.println)
    }

}