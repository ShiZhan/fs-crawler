object URIprocess {
  import org.apache.http.client.utils.URIBuilder

  def main(args: Array[String]) = {
    val str = "r:\\Internet 临时文件\\Low\\Content.IE5\\XZC7FD6F\\T179zzFnhXXXXO75vn-225-90[1].jpg"
    val str1 = str.replace('\\', '/')
    val str2 = "/home/user/foo/测试 空格/test"
    val str3 = "r:\\TEMP/home/user/foo/测试 空格/test"
    val ub = new URIBuilder
    ub.setHost(java.net.InetAddress.getLocalHost.getHostName)
    ub.setScheme("trigram")
    ub.setPath(str1)
    println(str1)
    println(ub)
    ub.setPath(str2)
    println(str2)
    println(ub)
    val str4 = str3.replace('\\', '/')
    ub.setPath(str4)
    println(str3)
    println(str4)
    println(ub)
  }
}