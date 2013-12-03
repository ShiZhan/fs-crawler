object TestDirectory {

  import java.io.{ File, PrintWriter }
  import scala.util.Random

  def randomStr(length: Int) = {
    val chars = ('a' to 'z') ++ ('A' to 'Z')
    (0 to length) map { c => chars(Random.nextInt(chars.length)) } mkString
  }

  def createFile(name: String) = {
    val p = new PrintWriter(new File(name))
    p.print(randomStr(1024))
    p.close
  }

  def createDir(name: String) = new File(name).mkdir
  def createDirs(name: String) = new File(name).mkdirs

  class Names[T](nList: Seq[String]) {
    def in(parent: String) = nList map { parent + '/' + _ }
    def mkdir = nList foreach createDir
    def mkdirs = nList foreach createDirs
    def create = nList foreach createFile
  }

  implicit def names[T](nList: Seq[String]) = new Names(nList)

  def names(n: Int) = (1 to n) map { i => "%08x".format(i) }

//  def createLevel(root: Seq[String], levels: List[Int]): Unit = {
//    levels match {
//      case current :: next => {
//        root.mkdir
//        val currentLevel = root flatMap { names(current) in _ }
//        createLevel(currentLevel, next)
//      }
//      case Nil => root.create
//    }
//  }

  def main(args: Array[String]) = {
    if (args.length < 3)
      println("usage: TestDirectory <target directory> <level1> ...")
    else {
      val root = Seq(args(0) + "/test")
      val levels = args.drop(1).map(_.toInt).toList
      //createLevel(root, levels)

      (root /: levels) { (r, l) => r.mkdir; r flatMap { names(l) in _ } }.create

      levels.zipWithIndex.foreach {
        case (l, i) =>
          val levelTotal = (1 /: levels.take(i + 1)) { (r, c) => r * c }
          println("Level " + i + ':' + levelTotal)
      }
    }
  }

}