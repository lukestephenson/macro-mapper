ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.7"

ThisBuild / scalacOptions ++= Seq(
)

inThisBuild(List(
  organization := "io.github.lukestephenson",
  homepage := Some(url("https://github.com/lukestephenson/macro-mapper")),
  // Alternatively License.Apache2 see https://github.com/sbt/librarymanagement/blob/develop/core/src/main/scala/sbt/librarymanagement/License.scala
  licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  developers := List(
    Developer(
      "lukestpehenson",
      "Luke Stephenson",
      "luke.stephenson@gmail.com",
      url("https://github.com/lukestephenson")
    )
  )
))

ThisBuild / githubWorkflowJavaVersions := List(JavaSpec.temurin("17"), JavaSpec.temurin("25"))
ThisBuild / githubWorkflowJavaVersions += JavaSpec.temurin("17")

ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches :=
  Seq(RefPredicate.StartsWith(Ref.Tag("v")))

ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Sbt(
    commands = List("ci-release"),
    name = Some("Publish project"),
  )
)

lazy val root = (project in file("."))
  .settings(
    name := "macro-mapper"
  )

libraryDependencies += "org.typelevel" %% "cats-core" % "2.13.0"

libraryDependencies += "org.scalameta" %% "munit" % "1.3.0" % Test
