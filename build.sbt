ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "Consulta02",
    idePackagePrefix := Some("ec.edu.utpl.computacion.c2"),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.8.0",
      "com.typesafe.akka" %% "akka-stream" % "2.8.0",
      "com.typesafe.akka" %% "akka-stream-testkit" % "2.8.0" % Test
    )
  )
