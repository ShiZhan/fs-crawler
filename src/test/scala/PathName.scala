object PathName {
  import java.io.File
  import scala.util.Properties.{ envOrElse, userDir }

  def UNC(fileName: String) = {
    val f = new File(fileName)
    val uri = f.toURI.toString
    if (System.getProperty("os.name").startsWith("Windows"))
      f.toURI.toString.replaceFirst("file:/", "")
    else
      f.toURI.toString.replaceFirst("file:", "")
  }

  val _PWD = userDir
  val _TGMROOT = envOrElse("TGM_ROOT", _PWD)
  val TGMROOT = UNC(_TGMROOT)
  val TGMDATA = UNC(envOrElse("TGM_DATA", _PWD)) + ".trigram"
  val CIMDATA = UNC(envOrElse("CIM_DATA", _TGMROOT)) + "cim/"

  def main(args: Array[String]) = {
    println(".:    " + UNC("."))
    println("..:   " + UNC(".."))
    println("PWD:  " + _PWD)
    println("ROOT: " + TGMROOT)
    println("DATA: " + TGMDATA)
    println("CIM:  " + CIMDATA)
  }
}