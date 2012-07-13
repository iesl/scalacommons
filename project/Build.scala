import sbt._
import edu.umass.cs.iesl.sbtbase.{CleanLogging, Dependencies, IeslProject}
import edu.umass.cs.iesl.sbtbase.IeslProject._

object ScalaCommonsBuild extends Build {

  val vers = "0.1-SNAPSHOT"

  val allDeps = new Dependencies(CleanLogging.excludeLoggers)

  import allDeps._

  val deps = Seq(
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
    mavenFindbugs()) ++ standardLogging


  lazy val scalacommons = IeslProject("scalacommons", vers, deps, Public)

}
