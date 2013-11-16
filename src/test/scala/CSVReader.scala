import java.io.{ File, PrintWriter }

class CSVReader(csvFile: File, delimiter: Char) {
  private val f = io.Source.fromFile(csvFile)
  private val lines = f.getLines
  val iterator = f.getLines.map { _.split(delimiter) }
}

object DIYCSVReader {
  /**
   *  test my own CSV reader
   */
  val sample =
    """0deb75a703b0129bbd1270e51494668c;r:\TEMP/Cache/_CACHE_MAP_
95206ac415d1578dbd6ef79ec9c7155d;r:\TEMP/Cache/0/E9/2D476d01
a6e86aec0475cb8ddeb06df5822b22b6;r:\TEMP/Cache/0/B4/8DF03d01
32c0ad5badb02447e62b5a8c81ba00a9;r:\TEMP/Cache/0/1F/5FF3Cd01
290e32edf04f8ec32906062749475194;r:\TEMP/Cache/0/79/7DE41d01
2cd19a9b82d730c3a69a5857826ab20d;r:\TEMP/Cache/0/EB/9B8C4d01
fe433707d9d7853f982bb882dd505d77;r:\TEMP/Cache/1/39/A9B66d01
14d9ad343801c6183158cc58928bcc65;r:\TEMP/Cache/1/B2/FB2A8d01
5ae4ad249d98c5889c7d1be7f0cd25c7;r:\TEMP/Cache/1/0B/61D2Bd01
541dfa14c31fbdd820c876ac98870240;r:\TEMP/Cache/1/A5/815FFd01
5528db7a9f18bb7233fa1891d7aa2b3e;r:\TEMP/Cache/1/BB/C9413d01
07d34ab6af581ed35d9445d09b65b589;r:\TEMP/Cache/1/F2/A8C80d01
9620a9ca11d916a159234026f2d76f57;r:\TEMP/Cache/1/CF/25453d01
570030a99c446e06d94a7fc3c0790c32;r:\TEMP/Cache/2/7F/8A8CDd01
4d8045810b12009e56c660b36ec0697e;r:\TEMP/Cache/2/35/75C87d01
060bad9dc6f50fc6be2b27e41721724c;r:\TEMP/Cache/2/5F/8D110d01
5be8da66a2ede16edd3ab3f311a24542;r:\TEMP/Cache/2/9E/80BE0d01"""

  def main(args: Array[String]) = {
    val f = new File("r:/TEMP/CSVTest")
    val p = new PrintWriter(f)
    p.write(sample)
    p.close

    val csv = new CSVReader(f, ';')
    val entries = csv.iterator
    for (e <- entries) println(e.mkString("; "))

  }
}