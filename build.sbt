name := """json-formatter-snowplow"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.6"
val circeVersion = "0.14.1"

libraryDependencies += guice
libraryDependencies += jdbc
libraryDependencies += "org.playframework.anorm" %% "anorm-postgres" % "2.6.10"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "org.postgresql" % "postgresql" % "9.4-1200-jdbc41"
libraryDependencies += "com.github.java-json-tools" % "json-schema-validator" % "2.2.14"


libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)


