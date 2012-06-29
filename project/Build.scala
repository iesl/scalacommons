import sbt._
import sbt.Keys._
import Dependencies._

object ScalaCommonsBuild extends Build {

  val vers = "0.1-SNAPSHOT"

  val deps = libraryDependencies ++= Seq(
    logbackClassic(),
    logbackCore(),
    slf4s(),
    dsutils(),
    commonsIo(),
    dispatchCore(),
    dispatchHttp(),
    classutil(),
    scalaCompiler(),
    scalatest(),
    scalazCore(),
    specs2(),
    scalaIoCore(),
    scalaIoFile(),
    jdom("1.1.3"),
    mavenCobertura(),
    mavenFindbugs())

  lazy val scalacommons = Project("scalacommons", file("."))
    .settings(scalaSettings: _*)
    .settings(resolvers ++= IESLRepos)
    .settings(//name := "scalacommons",
    organization := iesl,
    version := vers,
    scalaVersion := scalaV,
    deps,
    publishToIesl(vers, Public),
    creds
  )

}
