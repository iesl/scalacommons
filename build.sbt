name := "scalacommons"

organization := "edu.umass.cs.iesl"

version := "0.1-SNAPSHOT"

scalaVersion := "2.9.1"

resolvers ++= Seq(
  "sbt-idea-repo" at "http://mpeltonen.github.com/maven/",
  "David Soergel Repo" at "http://dev.davidsoergel.com/nexus/content/groups/public",
  "David Soergel Snapshots" at "http://dev.davidsoergel.com/nexus/content/repositories/snapshots",
  "IESL Repo" at "https://dev-iesl.cs.umass.edu/nexus/content/groups/public"
)

libraryDependencies ++= Seq(
  "ch.qos.logback"                % "logback-classic"    % "0.9.24",
  "ch.qos.logback"                % "logback-core"       % "0.9.24",
  "com.davidsoergel"              % "dsutils"            % "1.04-SNAPSHOT" exclude("commons-logging", "commons-logging"),
  "com.github.scala-incubator.io" %% "scala-io-core"     % "0.2.0",
  "com.github.scala-incubator.io" %% "scala-io-file"     % "0.2.0",
  "com.weiglewilczek.slf4s"       %% "slf4s"             % "1.0.7",
  "commons-io"                    %  "commons-io"        %  "2.0.1",
  "net.databinder"                %% "dispatch-core"     %  "0.8.7",
  "net.databinder"                %% "dispatch-http"     %  "0.8.7",
  "org.clapper"                   %% "classutil"         % "0.4.3",
  "org.jdom"                      %  "jdom"              %  "1.1",
  "org.scala-lang"                % "scala-compiler"     % "2.9.1",
  "org.scalatest"                 %% "scalatest"         % "1.6.1" % "test",
  "org.scalaz"                    %% "scalaz-core"       %  "6.0.4",
  "org.specs2"                    %% "specs2"            %  "1.7.1"  %  "test"
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
