object PrepareTestDirectory {
  import java.io.File
  import java.io.PrintWriter

  def main(args: Array[String]) = {
    if (args.length < 1)
      println("usage: PrepareTestDirectory <target directory>")
    else {
      val root = args(0)+ "/test"
      val test = new File(root)
      test.mkdirs

      (1 to 10) map {i => new File(root + "/dir" + "%03d".format(i))} foreach {_.mkdir}

      val genericFile = new PrintWriter(root + "/generic-file")
      genericFile.println("This is a genereic file.")
      genericFile.close

      val readOnlyFile = new File(root + "/read-only-file")
      val readOnlyFileWriter = new PrintWriter(readOnlyFile)
      readOnlyFileWriter.println("This is a read only file.")
      readOnlyFileWriter.close
      readOnlyFile.setReadOnly

      println("test directory ready")
    }
  }
}
