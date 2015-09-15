name := "yaml4s"

version := "0.1.0-SNAPSHOT"

organization := "com.grandata"

scalaVersion := "2.11.7"

crossScalaVersions := Seq("2.10.4", "2.11.7")

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation")

scalacOptions in Test ++= Seq("-Yrangepos")


credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

publishTo := {
  val url = "https://nexus.grandata.com/content/repositories"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("Grandata Snapshots" at url + "/snapshots")
  else
    Some("Grandata Releases" at url + "/releases")
}


//resolvers += "Sonatype Nexus Repository Manager" at "https://nexus.grandata.com/content/repositories/releases"
resolvers += "Sonatype Nexus Repository Manager" at "https://nexus.grandata.com/content/repositories/snapshots"

libraryDependencies += "com.google.jimfs" % "jimfs" % "1.0" % "test"

libraryDependencies += "org.specs2" %% "specs2-core" % "3.6.2" % "test"

libraryDependencies += "org.specs2" %% "specs2-matcher-extra" % "3.6.2" % "test"

libraryDependencies += "org.specs2" %% "specs2-scalacheck" % "3.6.2" % "test"

libraryDependencies += "org.specs2" %% "specs2-mock" % "3.6.2" % "test"

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.12.4" % "test"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

scalacOptions in Test ++= Seq("-Yrangepos")

scalacOptions ++= Seq("-feature")

libraryDependencies += "org.json4s" %% "json4s-native" % "3.2.11"

libraryDependencies += "org.yaml" % "snakeyaml" % "1.15"

coverageEnabled.in(Test, test) := true