object SyncMan {

  import scala.io.Source
  import java.io.File
  import java.security.MessageDigest

  def md5(bytes: Iterator[Byte]) = {
    val md5 = MessageDigest.getInstance("MD5")
    try {
      bytes.foreach(md5.update)
//      md5.update(bytes.toArray)
    } catch {
      case e: Exception => throw e
    }
    md5.digest.map("%02x".format(_)).mkString
  }

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
    val md5sumList = chunks.map { md5 }.toArray
    fileBuffer.close
    md5sumList
  }

  def collect(dir: File, chunkSize: Int) = {

    def checkDir(d: File): Array[(String, String)] = {
      val (files, dirs) = d.listFiles.partition(_.isFile)
      val md5Files = files.flatMap { f =>
        val path = f.getAbsolutePath
        val md5File = fileMD5(f)
        val md5Chunks =
          if (f.length > chunkSize) {
            val list = chunkMD5(f, chunkSize).zipWithIndex
            list.map { case (m, i) => (m, path + "." + i) }
          } else
            Array[(String, String)]()
        Array((md5File, path)) ++ md5Chunks
      }
      md5Files ++ dirs.flatMap(checkDir)
    }
 
    checkDir(dir)
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
        val md5sums = collect(dir, args(1).toInt)
        for((m, p) <- md5sums) println(m + ';' +p)
      }
    }
  }

}