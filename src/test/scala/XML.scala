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