object chunk {

  import scala.io.Source
  import java.io.File
  import org.apache.commons.codec.digest.DigestUtils

  def main(args: Array[String]) =
    if (args.length < 2)
      println("run with <file> <chunk size>")
    else {
      val file = new File(args(0))
      val chunkSize = args(1).toInt
      val fBS = Source.fromFile(file, "ISO-8859-1")
      val chunks = fBS.grouped(chunkSize).map(_.map(_.toByte).toArray)
      for ((c, i) <- chunks.zipWithIndex) {
        println(i + ":\t" + DigestUtils.md5Hex(c))
      }
      fBS.close
    }

}