import sbt._
import Keys._
import edu.umass.cs.iesl.sbtbase.Dependencies
import edu.umass.cs.iesl.sbtbase.IeslProject._


object ScalaCommonsBuild extends Build {

  val vers = "0.2-SNAPSHOT"

  implicit val allDeps: Dependencies = new Dependencies();

  import allDeps._

  val deps = Seq(
    //dsutils(),
    "com.davidsoergel" % "dsutils" % "1.05",
    scalatime(),
    //def jodatime(v: String = "latest.release") = "joda-time" % "joda-time" % v applyGlobal()
    // def scalatime(v: String = "latest.release") = "com.github.nscala-time" %% "nscala-time" % v applyGlobal()
    
    "commons-io" % "commons-io" % "2.4",
    classutil(),
    scalaCompiler("2.11.2"),
    // scalatest(),
    "org.specs2" %% "specs2" % "2.4.1" % "test",
    //specs2("2.4.1"),
    "com.github.scala-incubator.io" %% "scala-io-core" % "latest.release",
    "com.github.scala-incubator.io" %% "scala-io-file" % "latest.release",

    "com.typesafe" % "config" % "latest.release",
    "org.jdom" % "jdom" % "1.1.3",
    "org.scala-lang.modules" %% "scala-xml" % "1.0.1",

    //"com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",

    "ch.qos.logback" % "logback-classic"      % "latest.release",
    "ch.qos.logback" % "logback-core"         % "latest.release",
    "org.slf4j"      % "slf4j-api"            % "latest.release",
    //"com.typesafe"   %% "scalalogging-slf4j"  % "latest.release",
    "com.typesafe.scala-logging" %% "scala-logging" % "latest.release",
    "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "latest.release",
    "org.slf4j"      % "jcl-over-slf4j"       % "latest.release",
    "org.slf4j"      % "log4j-over-slf4j"     % "latest.release",
    "org.slf4j"      % "jul-to-slf4j"         % "latest.release",
    "org.clapper"    %% "grizzled-slf4j"      % "latest.release"
  )

  lazy val scalacommons = Project("scalacommons", file(".")) // .ieslSetup(
  .settings(
    scalaVersion := "2.11.2",
    //scalacOptions ++= Seq("-feature", "-deprecation"),
    crossScalaVersions := Seq("2.10.4", "2.11.2"),
    libraryDependencies ++= deps,
    libraryDependencies ++= (
      if (scalaVersion.value.startsWith("2.10")) Nil
      else Seq("org.scala-lang.modules" %% "scala-xml" % "1.0.1")
    )
  )

  // .cleanLogging.standardLogging


}



//class CleanLogging(deps: Dependencies) {
// 
//  import deps._
// 
//  def standardLogging(slf4jVersion:String="latest.release") = Seq(
//    // see http://www.slf4j.org/legacy.html
// 
//    // ultimately log everything via Logback
// 
//    logbackCore(),
//    logbackClassic(),
// 
//    // use the slf4j wrapper API
//    slf4j(slf4jVersion),
// 
//    // nice Scala syntax for slf4j
//    //slf4s(),
//    scalalogging(),
// 
//    // direct legacy Jakarta Commons Logging calls to slf4j
//    jclOverSlf4j(slf4jVersion),
// 
//    // direct legacy log4j calls to slf4j
//    log4jOverSlf4j(slf4jVersion),
// 
//    // direct legacy java.util.logging calls to slf4j
//    julToSlf4j(slf4jVersion),
// 
//    // direct grizzled-slf4j calls to slf4j
//    grizzledSlf4j(slf4jVersion)
//  )
// 
//}
