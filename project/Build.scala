import sbt._
import Keys._
import sbtrelease.ReleasePlugin._

//import edu.umass.cs.iesl.sbtbase.Dependencies
//import edu.umass.cs.iesl.sbtbase.IeslProject._


object ScalaCommonsBuild extends Build {

  val vers = "0.2-SNAPSHOT"

  //implicit val allDeps: Dependencies = new Dependencies();

  //import allDeps._
  val scalaV = "2.11.2"

  val deps = Seq(

    // scalatest(),
    //specs2("2.4.1"),

    "com.github.nscala-time" %% "nscala-time" % "latest.release",
    "org.clapper" %% "classutil" % "latest.release",
    "org.scala-lang"                % "scala-compiler" % scalaV,
    "com.davidsoergel"              % "dsutils" % "1.05",
    "org.specs2"                    %% "specs2" % "2.4.1" % "test",
    "commons-io"                    % "commons-io" % "2.4",
    "com.github.scala-incubator.io" %% "scala-io-core" % "latest.release",
    "com.github.scala-incubator.io" %% "scala-io-file" % "latest.release",
    "com.typesafe"                  % "config" % "latest.release",
    "org.jdom"                      % "jdom" % "1.1.3",
    "org.scala-lang.modules"        %% "scala-xml" % "1.0.1",
    "ch.qos.logback"                % "logback-classic"      % "latest.release",
    "ch.qos.logback"                % "logback-core"         % "latest.release",
    "org.slf4j"                     % "slf4j-api"            % "latest.release",
    //"com.typesafe"                %% "scalalogging-slf4j"  % "latest.release",
    "com.typesafe.scala-logging"    %% "scala-logging" % "latest.release",
    "com.typesafe.scala-logging"    %% "scala-logging-slf4j" % "latest.release",
    "org.slf4j"                     % "jcl-over-slf4j"       % "latest.release",
    "org.slf4j"                     % "log4j-over-slf4j"     % "latest.release",
    "org.slf4j"                     % "jul-to-slf4j"         % "latest.release",
    "org.clapper"                   %% "grizzled-slf4j"      % "latest.release"
  )

  // TODO undo this copypasta from iesl sbt base for deps, logging config
  lazy val scalacommons = Project("scalacommons", file("."))
  .settings(releaseSettings:_*)
  .settings(
    resolvers += "IESL Public Releases" at "https://dev-iesl.cs.umass.edu/nexus/content/groups/public",
    scalaVersion := "2.11.2",
    scalacOptions := Seq(
      "-Xlint", "-deprecation", "-unchecked", "-Xcheckinit",
      "-g:vars",
      "-encoding", "utf8",
      "-feature",
      "-language:reflectiveCalls",
      "-language:implicitConversions", "-language:postfixOps"
    ),
    javacOptions ++= Seq("-Xlint:unchecked", "-encoding", "utf8"),
    crossScalaVersions := Seq("2.10.4", "2.11.2"),
    libraryDependencies ++= deps,
    libraryDependencies ++= (
      if (scalaVersion.value.startsWith("2.10")) Nil
      else Seq("org.scala-lang.modules" %% "scala-xml" % "1.0.1")
    )
  )

  // .cleanLogging.standardLogging


}

