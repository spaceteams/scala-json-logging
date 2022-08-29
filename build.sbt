lazy val scala_2_13 = "2.13.8"
lazy val scala_3_1 = "3.1.1"
lazy val supportedScalaVersions = Seq(scala_2_13, scala_3_1)

Global / semanticdbEnabled := true
Global / semanticdbVersion := scalafixSemanticdb.revision

ThisBuild / organization := "de.spaceteams"
ThisBuild / licenses := Seq(
  ("BSD-3", url("https://opensource.org/licenses/BSD-3-Clause"))
)
ThisBuild / homepage := Some(
  url("http://github.com/spaceteams/scala-json-logging")
)
ThisBuild / description := "An slf4j scala backend for cloud native environments"
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("http://github.com/spaceteams/scala-json-logging"),
    "scm:git:http://github.com/spaceteams/scala-json-logging.git",
    "scm:git:git@github.com:spaceteams/scala-json-logging.git"
  )
)

ThisBuild / developers := List(
  Developer(
    "kampka",
    "Christian Kampka",
    "christian.kampka@spaceteams.de",
    url("https://www.spaceteams.de")
  )
)

ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / sonatypeRepository := "https://s01.oss.sonatype.org/service/local"

ThisBuild / scalaVersion := scala_2_13

ThisBuild / version := "1.0.1"
ThisBuild / versionScheme := Some("semver-spec")
ThisBuild / fork := true
ThisBuild / useCoursier := true

ThisBuild / scalafixScalaBinaryVersion := scalaBinaryVersion.value
ThisBuild / scalafixDependencies := Seq(
  "com.github.liancheng" %% "organize-imports" % "0.6.0"
)

ThisBuild / libraryDependencies ++= (CrossVersion
  .partialVersion(scalaVersion.value) match {
  case Some((2, 13)) =>
    Seq(
      "org.scala-lang" % "scala-library" % scalaVersion.value % Provided
    )
  case _ =>
    Seq(
      "org.scala-lang" % "scala3-library_3" % scalaVersion.value % Provided
    )
}) ++ Seq(
  "org.slf4j" % "slf4j-api" % "2.0.0" % Provided,
  "org.scalatest" %% "scalatest" % "3.2.11" % Test
)

lazy val commonSettings = Seq(
  scalacOptions := (if (scalaVersion.value.startsWith("3"))
                      Seq("-Xfatal-warnings", "-Ykind-projector")
                    else Seq("-Werror", "-Wunused")) ++ Seq(
    "-deprecation",
    "-feature"
  ),
  crossScalaVersions := supportedScalaVersions
)

lazy val common = (project in file("common"))
  .settings(commonSettings)
  .settings(
    moduleName := "json-logging-common"
  )

lazy val circe = (project in file("circe"))
  .dependsOn(common)
  .settings(commonSettings)
  .settings(
    moduleName := "json-logging-circe",
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % "0.14.1" % Provided,
      "io.circe" %% "circe-parser" % "0.14.1" % Test
    )
  )

lazy val spray = (project in file("spray"))
  .dependsOn(common)
  .settings(commonSettings)
  .settings(
    moduleName := "json-logging-spray",
    libraryDependencies += "io.spray" %% "spray-json" % "1.3.6" % Provided
  )

lazy val typesafeConfig = (project in file("typesafe-config"))
  .dependsOn(common, spray % Test)
  .settings(commonSettings)
  .settings(
    moduleName := "json-logging-typesafe-config",
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.4.2" % Provided,
      "io.spray" %% "spray-json" % "1.3.6" % Test
    )
  )

lazy val root = (project in file("."))
  .aggregate(common, circe, spray, typesafeConfig)
  .settings(
    publish / skip := true
  )
