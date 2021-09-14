name := """json-formatter-snowplow"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.6"
val circeVersion = "0.14.1"
val slickVersion = "3.3.3"

libraryDependencies += guice
libraryDependencies += jdbc
libraryDependencies += jdbc % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "org.postgresql" % "postgresql" % "9.4-1200-jdbc41"
libraryDependencies += "com.github.java-json-tools" % "json-schema-validator" % "2.2.14"
libraryDependencies += "com.h2database" % "h2" % "1.4.192"

// Slick dependencies
libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick",
  "com.typesafe.slick" %% "slick-hikaricp"
).map(_ % slickVersion)

// circle dependencies
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)


