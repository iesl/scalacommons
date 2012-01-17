name := "scalacommons"

organization := "edu.umass.cs.iesl"

version := "0.1-SNAPSHOT"

scalaVersion := "2.9.1"

resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"

resolvers += "David Soergel Repo" at "http://dev.davidsoergel.com/artifactory/repo"

libraryDependencies += "com.davidsoergel" % "dsutils" % "1.03"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.6.1" % "test"

libraryDependencies += "com.weiglewilczek.slf4s" %% "slf4s" % "1.0.7"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "0.9.24"

libraryDependencies += "ch.qos.logback" % "logback-core" % "0.9.24"

libraryDependencies += "com.github.scala-incubator.io" %% "scala-io-core" % "0.2.0"

libraryDependencies += "com.github.scala-incubator.io" %% "scala-io-file" % "0.2.0"

libraryDependencies += "org.scala-lang" % "scala-compiler" % "2.9.1"

publishTo <<= (version)
                                            {version: String =>
                                              {
                                              def repo(name: String) = name at "http://iesl.cs.umass.edu:8081/nexus/content/repositories/" + name
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
                                      Credentials("Sonatype Nexus Repository Manager", "iesl.cs.umass.edu", user, pass)
                                    case _ =>
                                      Credentials(Path.userHome / ".ivy2" / ".credentials")
                                  }
                                  }
