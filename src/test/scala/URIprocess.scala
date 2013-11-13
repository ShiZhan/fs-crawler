object URIprocess {
  import java.io.File
  import org.apache.http.client.utils.URIBuilder
  import com.hp.hpl.jena.util.FileUtils
  import util.Platform.isWindows

  def fileName2URI(fileName: String) = {
    val n = FileUtils.toURL(fileName)
    if (fileName.head == '/' && isWindows) n.drop(10) else n.drop(7)
  }

  def pathString2URI(path: String) = {
    val rootUNC = if (path.head.isLetter) ('/' + path) else path
    val posix = rootUNC.replace('\\', '/')
    val f = new File(posix)
    val trimming = if (isWindows) 8 else 5
//    val trimming = 5
    f.toURI.toString.substring(trimming)
  }

  val str1 = "r:\\Internet 临时文件\\Low\\Content.IE5\\XZC7FD6F\\T179zzFnhXXXXO75vn-225-90[1].jpg"
  val str2 = "/home/user/foo/测试 [空格]/test"
  val str3 = "R:\\TEMP/home/user/f{o}o/测试 (空格)/test"

  def main(args: Array[String]) = {
    println(str1)
    println(str2)
    println(str3)
    println("---")

    val ub = new URIBuilder
    ub.setHost(java.net.InetAddress.getLocalHost.getHostName)
    ub.setScheme("trigram")
    println(ub.setPath(str1))
    println(ub.setPath(str2))
    println(ub.setPath(str3))
    println("---")

    println(fileName2URI(str1))
    println(fileName2URI(str2))
    println(fileName2URI(str3))
    println("---")

    println(pathString2URI(str1))
    println(pathString2URI(str2))
    println(pathString2URI(str3))
  }
}