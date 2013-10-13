object GenerateProtegeCatalog {

  import java.io.{ File, PrintWriter }
  import scala.collection.JavaConversions._

  def getBaseURI(model: String) = {
    val txt = io.Source.fromFile(model).getLines.filter{_.contains("<owl:Ontology rdf:about=")}
    """\".*\"""".r.findFirstIn(txt.mkString).get
  }

  val header = """<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<catalog prefer="public" xmlns="urn:oasis:names:tc:entity:xmlns:xml:catalog">"""
  val uriLn = """
    <uri id="User Entered Import Resolution" name=%s uri="%s"/>"""
  val footer = """
    <group id="Folder Repository, directory=, recursive=true, Auto-Update=true, version=1" prefer="public" xml:base=""/>
</catalog>"""

  def main(args: Array[String]) =
    if (args.length < 1)
      println("run with <model>")
    else {
      val repo = new File(args(0))
      val owls = repo.list.filter(_.endsWith(".owl"))
      val uris = owls map { o => uriLn.format(getBaseURI(args(0) + "/" + o), o) }
      val cat = new PrintWriter(args(0) + "/catalog-v001.xml")
      cat.print(header)
      cat.print(uris.mkString)
      cat.print(footer)
      cat.close
    }

}
