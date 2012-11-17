import sbt._
import edu.umass.cs.iesl.sbtbase.Dependencies
import edu.umass.cs.iesl.sbtbase.IeslProject._

object ScalaCommonsBuild extends Build {

  val vers = "0.1-SNAPSHOT"

  implicit val allDeps: Dependencies = new Dependencies(); //(CleanLogging.excludeLoggers)  // doesn't work?

  import allDeps._

  val deps = Seq(
    dsutils(),
    commonsIo(),
    classutil(),
    scalaCompiler(),
    scalatest(),
    specs2(),
    scalaIoCore("0.4.0"),
    scalaIoFile("0.4.0"),
    "com.typesafe" % "config" % "latest.release", // TODO allDeps.typesafeConfig(),
    jdom("1.1.3"),
    mavenCobertura(),
    mavenFindbugs())


  lazy val scalacommons = Project("scalacommons", file(".")).ieslSetup(vers, deps, Public).cleanLogging.standardLogging()

}
