import sbt._
import edu.umass.cs.iesl.sbtbase.IeslProject
import edu.umass.cs.iesl.sbtbase.IeslProject._
import edu.umass.cs.iesl.sbtbase.Dependencies._

object ScalaCommonsBuild extends Build {

  val vers = "0.1-SNAPSHOT"

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
    mavenFindbugs())


  lazy val scalacommons = IeslProject("scalacommons", vers, deps, Public)

}
