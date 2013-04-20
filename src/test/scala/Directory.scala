/**
 *
 */
/**
 * @author ShiZhan
 *
 */
import scalax.file.{ Path, PathSet }

object DirectoryFull {

  def main(args: Array[String]) =
    if (args.length < 1)
      println("run with <directory>")
    else {
      val ps = Path(args(0)) ** "*"
      ps.foreach(println)
      println("Total: " + ps.size)
    }

}

object DirectoryCurrent {

  def main(args: Array[String]) =
    if (args.length < 1)
      println("run with <directory>")
    else {
      val ps = Path(args(0)) * "*"
      ps.foreach(println)
      println("Total: " + ps.size)
    }

}