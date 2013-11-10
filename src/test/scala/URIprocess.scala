object URIprocess {
  import org.apache.http.client.utils.URIBuilder

  def main(args: Array[String]) = {
    val str = "r:\\Internet 临时文件\\Low\\Content.IE5\\XZC7FD6F\\T179zzFnhXXXXO75vn-225-90[1].jpg"
    val str1 = str.replace('\\', '/')
    val uri = new URIBuilder()
    uri.setPath(str1)
    println(str1)
    println(uri)
  }
}