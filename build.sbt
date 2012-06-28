import Dependencies._

name := "scalacommons"

organization := "edu.umass.cs.iesl"

version := "0.1-SNAPSHOT"

scalaVersion := "2.9.1"

resolvers ++= Seq(
  "IESL Repo" at "https://dev-iesl.cs.umass.edu/nexus/content/groups/public",
  "IESL Repo" at "https://dev-iesl.cs.umass.edu/nexus/content/groups/public-snapshots"
)

libraryDependencies ++= Seq(
  "ch.qos.logback"                % "logback-classic"    % "1.0.6",
  "ch.qos.logback"                % "logback-core"       % "1.0.6",
  "com.davidsoergel"              % "dsutils"            % "1.04-SNAPSHOT" exclude("commons-logging", "commons-logging"),
  "com.weiglewilczek.slf4s"       % "slf4s_2.9.1"        % "1.0.7",
  "commons-io"                    %  "commons-io"        % "2.3",
  "net.databinder"                %% "dispatch-core"     % "0.8.8",
  "net.databinder"                %% "dispatch-http"     % "0.8.8",
  "org.clapper"                   %% "classutil"         % "0.4.6",
  "org.scala-lang"                %  "scala-compiler"    % "2.9.1",
  "org.scalatest"                 %% "scalatest"         % "1.8" % "test",
  "org.scalaz"                    %% "scalaz-core"       % "6.0.4",
  "org.specs2"                    %% "specs2"            % "1.11" % "test",
  "com.github.scala-incubator.io" %% "scala-io-core"     % "0.4.0",
  "com.github.scala-incubator.io" %% "scala-io-file"     % "0.4.0",
  "org.jdom"                      %  "jdom"              % "2.0.2",
  mavenCobertura,
  mavenFindbugs
)

publishTo <<= (version)
    {
    version: String =>
        {
        def repo(name: String) = name at "https://dev-iesl.cs.umass.edu/nexus/content/repositories/" + name
        val isSnapshot = version.trim.endsWith("SNAPSHOT")
        val repoName = if (isSnapshot) "snapshots" else "releases"
        Some(repo(repoName))
        }
    }

credentials +=
    {
    Seq("build.publish.user", "build.publish.password").map(k => Option(System.getProperty(k))) match
        {
        case Seq(Some(user), Some(pass)) =>
            Credentials("Sonatype Nexus Repository Manager", "dev-iesl.cs.umass.edu", user, pass)
        case _ =>
            Credentials(Path.userHome / ".ivy2" / ".credentials")
        }
    }
