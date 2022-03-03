lazy val scala_2_13 = "2.13.8"
lazy val scala_3_1 = "3.1.1"
lazy val supportedScalaVersions = Seq(scala_2_13, scala_3_1)


Global / organization := "de.spaceteams"
Global / licenses := Seq(("BSD-3", url("https://www.apache.org/licenses/LICENSE-2.0.html")))
Global / homepage := Some(url("http://github.com/spaceteams/scala-json-logging"))
Global / description := "An slf4j scala backend for cloud native environments"
Global / scmInfo := Some(
  ScmInfo(
    url("http://github.com/spaceteams/scala-json-logging"),
    "scm:git:http://github.com/spaceteams/scala-json-logging.git",
    "scm:git:git@github.com:spaceteams/scala-json-logging.git"))

ThisBuild / scalaVersion := scala_2_13
ThisBuild / version := "0.99.0"
ThisBuild / fork := true
ThisBuild / useCoursier := true

ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision
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
  "org.slf4j" % "slf4j-api" % "2.0.0-alpha6" % Provided,
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
    publish := false
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
    libraryDependencies ++= Seq(
      "io.spray" %% "spray-json" % "1.3.6" % Provided
    )
  )
