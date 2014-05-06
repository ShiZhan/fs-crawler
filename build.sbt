name := "FSCrawler"

version := "1.0"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
 "org.apache.jena" % "jena-core" % "2.11.1" excludeAll(ExclusionRule(organization = "org.slf4j")), 
 "org.slf4j" % "slf4j-api" % "1.7.5",
 "org.slf4j" % "slf4j-log4j12" % "1.7.5",
 "log4j" % "log4j" % "1.2.17",
 "commons-codec" % "commons-codec" % "1.9",
 "org.apache.commons" % "commons-compress" % "1.8"
)
