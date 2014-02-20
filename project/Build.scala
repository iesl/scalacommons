import sbt._
import edu.umass.cs.iesl.sbtbase.Dependencies
import edu.umass.cs.iesl.sbtbase.IeslProject._


object ScalaCommonsBuild extends Build {

  val vers = "0.2-SNAPSHOT"

  implicit val allDeps: Dependencies = new Dependencies();

  import allDeps._

  val deps = Seq(
    dsutils(),
    scalatime(),
    commonsIo(),
    classutil(),
    scalaCompiler(),
    scalatest(),
    specs2("2.1"),
    scalaIoCore("0.4.2"),
    "com.typesafe" % "config" % "latest.release", 
    jdom("1.1.3")
    // scalaIoFile("0.4.2"),
    // mavenCobertura(),
    // mavenFindbugs()
  )

  lazy val scalacommons = Project("scalacommons", file(".")).ieslSetup(vers, deps, Public).cleanLogging.standardLogging

}
