name := "scalautils"

organization := "edu.umass.cs.iesl"

version := "0.1-SNAPSHOT"

scalaVersion := "2.9.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.6.1" % "test"

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
