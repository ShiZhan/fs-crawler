object SyncMan {

  import scala.io.Source
  import java.io.{ File, InputStream, PrintWriter }
  import java.security.MessageDigest

  def md5(bytes: Iterator[Byte]) = {
    val md5 = MessageDigest.getInstance("MD5")
    try {
      bytes.foreach(md5.update)
    } catch {
      case e: Exception => throw e
    }
    md5.digest.map("%02x".format(_)).mkString
  }

  //  def md5(bytes: Iterator[Byte]) = {
  //    val md5 = MessageDigest.getInstance("MD5")
  //    try {
  //      md5.update(bytes.toArray)
  //    }
  //    catch {
  //      case e: Exception => throw e
  //    }
  //    md5.digest.map("%02x".format(_)).mkString
  //  }

  def fileMD5(file: File) = {
    val fileBuffer = Source.fromFile(file, "ISO-8859-1")
    val fileBytes = fileBuffer.map(_.toByte)
    val md5sum = md5(fileBytes)
    fileBuffer.close
    md5sum
  }

  def chunkMD5(file: File, chunkSize: Int) = {
    val fileBuffer = Source.fromFile(file, "ISO-8859-1")
    val chunks = fileBuffer.grouped(chunkSize).map(_.map(_.toByte).iterator)
    val md5sumList = chunks.map { md5 }.toList
    fileBuffer.close
    md5sumList
  }

  def main(args: Array[String]) = {
    if (args.length < 2)
      println("usage: SyncMan <directory> <chunk size>")
    else {
      val dir = new File(args(0))
      if (!dir.exists)
        println("input source does not exist")
      else if (dir.isFile)
        println("input source is file")
      else {
        val chunkSize = args(1).toInt
        val dirName = dir.getName
        val sumFile = new PrintWriter(dirName + "-sum.csv")
 
        def checkDir(d: File): Unit = {
          val items = d.listFiles
          val (files, dirs) = items.partition(_.isFile)
          for (f <- files) {
            val path = f.getAbsolutePath
            val md5 = fileMD5(f)
            val md5List =
              if (f.length > chunkSize) chunkMD5(f, chunkSize).zipWithIndex else Nil
  
            sumFile.println(md5 + ";" + path)
            md5List.foreach { case (m, i) => sumFile.println(m + ";" + path + "." + i) }
          }
          dirs.foreach(checkDir)
        }

        checkDir(dir)

        sumFile.close
      }
    }
  }

}