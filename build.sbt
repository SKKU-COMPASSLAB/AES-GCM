// See README.md for license details.

ThisBuild / scalaVersion     := "2.13.12"
ThisBuild / version          := "0.1.0"
ThisBuild / organization     := "io.github.talha-ahmed-1"

val chiselVersion = "6.2.0"

lazy val root = (project in file("."))
  .settings(
    name := "aes-gcm",
    libraryDependencies ++= Seq(
      "org.chipsalliance" %% "chisel" % chiselVersion,
      "org.scalatest" %% "scalatest" % "3.2.16" % "test",
      // "org.chipsalliance" %% "chiselsim" % "1.0.0" % "test",
    ),
    scalacOptions ++= Seq(
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-Xcheckinit",
      "-Ymacro-annotations",
    ),
    addCompilerPlugin("org.chipsalliance" % "chisel-plugin" % chiselVersion cross CrossVersion.full),
  )


// scalaVersion := "2.12.13"

// scalacOptions ++= Seq(
//   "-feature",
//   "-language:reflectiveCalls",
// )

// // Chisel 3.5
// addCompilerPlugin("edu.berkeley.cs" % "chisel3-plugin" % "3.5.6" cross CrossVersion.full)
// libraryDependencies += "edu.berkeley.cs" %% "chisel3" % "3.5.6"
// libraryDependencies += "edu.berkeley.cs" %% "chiseltest" % "0.5.6"