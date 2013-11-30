object SyncMan {

  import scala.io.Source
  import java.io.{ File, InputStream, PrintWriter }
  import java.security.MessageDigest

  def md5(bytes: Iterator[Byte]) = {
    val md5 = MessageDigest.getInstance("MD5")
    try {
      bytes.foreach(md5.update)
    }
    catch {
      case e: Exception => throw e
    }
    md5.digest.map("%02x".format(_)).mkString
  }

//  def md5(bytes: Iterator[Byte]) = {
//    val blkLen = 65536
//    val blocks = bytes.grouped(blkLen).map(_.toArray)
//    val md5 = MessageDigest.getInstance("MD5")
//    try {
//      blocks.foreach(md5.update)
//    }
//    catch {
//      case e: Exception => throw e
//    }
//    md5.digest.map("%02x".format(_)).mkString
//  }

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

  def fileMD5(fileName: String) = {
    val file = new File(fileName)
    val fileBuffer = Source.fromFile(file, "ISO-8859-1")
    val fileBytes = fileBuffer.map(_.toByte)
    val md5sum = md5(fileBytes)
    fileBuffer.close
    md5sum
  }

  def chunkMD5(fileName: String, chunkSize: Int) = {
    val file = new File(fileName)
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
      val md5 = fileMD5(args(0))
      val md5List = chunkMD5(args(0), args(1).toInt).zipWithIndex
      println("MD5: " + md5)
      println("MD5 of chunks:")
      md5List.foreach(println)
    }
  }

}