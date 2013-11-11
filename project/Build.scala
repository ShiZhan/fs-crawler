import sbt._
import Keys._

object MyBuild extends Build {

  lazy val copyDependencies = TaskKey[Unit]("copy-dep")

  def copyDepTask = copyDependencies <<= (update, crossTarget, scalaVersion) map {
    (updateReport, out, scalaVer) =>
    updateReport.allFiles foreach { srcPath =>
      val destPath = out / "lib" / srcPath.getName
      IO.copyFile(srcPath, destPath, preserveLastModified=true)
    }
  }

  lazy val trigram = Project(
    id = "trigram",
    base = file("."),
    settings = Defaults.defaultSettings ++ Seq(
      copyDepTask
    )
  )

  lazy val accessory = Project(
    id = "accessory",
    base = file("accessory"),
    settings = Defaults.defaultSettings ++ Seq(
      copyDepTask
    )
  )

  lazy val chart = Project(
    id = "chart",
    base = file("chart"),
    settings = Defaults.defaultSettings ++ Seq(
      copyDepTask
    )
  )
}
