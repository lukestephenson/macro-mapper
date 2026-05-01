ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.7"

ThisBuild / scalacOptions ++= Seq(
)

ThisBuild / githubWorkflowJavaVersions += JavaSpec.temurin("17")

lazy val root = (project in file("."))
  .settings(
    name := "macro-mapper"
  )

libraryDependencies += "org.typelevel" %% "cats-core" % "2.13.0"
