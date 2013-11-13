object URIprocess {
  import org.apache.http.client.utils.URIBuilder
  import com.hp.hpl.jena.util.FileUtils
  import util.Platform.isWindows

  def fileName2URI(fileName: String) = {
    val n = FileUtils.toURL(fileName)
    if (fileName.head == '/' && isWindows) n.drop(10) else n.drop(7)
  }

  val str1 = "r:\\Internet 临时文件\\Low\\Content.IE5\\XZC7FD6F\\T179zzFnhXXXXO75vn-225-90[1].jpg"
  val str2 = "/home/user/foo/测试 空格/test"
  val str3 = "r:\\TEMP/home/user/foo/测试 空格/test"

  def main(args: Array[String]) = {
    println(str1)
    println(str2)
    println(str3)

    val ub = new URIBuilder
    ub.setHost(java.net.InetAddress.getLocalHost.getHostName)
    ub.setScheme("trigram")
    ub.setPath(str1)
    println(ub)
    ub.setPath(str2)
    println(ub)
    ub.setPath(str3)
    println(ub)

    println(fileName2URI(str1))
    println(fileName2URI(str2))
    println(fileName2URI(str3))
  }
}