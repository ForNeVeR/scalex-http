import sbt._
import Keys._

trait Resolvers {
  val typesafe = "typesafe.com" at "http://repo.typesafe.com/typesafe/releases/"
  val iliaz = "iliaz.com" at "http://scala.iliaz.com/"
}

trait Dependencies {
  val scalaz = "org.scalaz" %% "scalaz-core" % "6.0.4"
  val specs2 = "org.specs2" %% "specs2" % "1.12"
  val guava = "com.google.guava" % "guava" % "11.0.2"
}

object ApplicationBuild extends Build with Resolvers with Dependencies {

  val appName = "scalex-http"
  val appVersion = "1.1"

  lazy val scalex = uri("git://github.com/fornever/scalex#master")
  //lazy val scalex = uri("/home/thib/scalex")

  val scalexHttp = Project(
	id = "scalexHttp",
	base = file("."),
	settings = Defaults.defaultSettings ++ play.Project.playScalaSettings ++ Seq(
	  name := appName,
	  version := appVersion,
	  scalaVersion := "2.10.3",
      resolvers ++= Seq(typesafe, iliaz),
      libraryDependencies ++= Seq(scalaz, guava),
      scalacOptions := Seq("-deprecation", "-unchecked"))) dependsOn scalex
}
