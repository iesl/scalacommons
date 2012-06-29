import sbt._
import sbt.Keys._

// import com.github.siasia._
// import WebPlugin._
// import PluginKeys._
// import Keys._

object Dependencies {

  val iesl = "edu.umass.cs.iesl"
  val scalaV = "2.9.1"

  // any needed 3rd-party repositories should by proxied in Nexus and added to the public group, so their artifacts will be available here automatically.
  val IESLRepos = Seq(
    "IESL Public Releases" at "https://dev-iesl.cs.umass.edu/nexus/content/groups/public",
    "IESL Public Snapshots" at "https://dev-iesl.cs.umass.edu/nexus/content/groups/public-snapshots",
    "IESL Private Releases" at "https://dev-iesl.cs.umass.edu/nexus/content/repositories/private-releases",
    "IESL Private Snapshots" at "https://dev-iesl.cs.umass.edu/nexus/content/repositories/private-snapshots"
  )

  sealed case class RepoType()

  object Public extends RepoType

  object Private extends RepoType

  def publishToIesl(vers: String, repotype: RepoType) = publishTo := {
    def repo(name: String) = name at "https://dev-iesl.cs.umass.edu/nexus/content/repositories/" + name
    val isSnapshot = vers.endsWith("SNAPSHOT")
    val isPrivate = if (repotype == Private) "private-" else ""
    val repoName = isPrivate + (if (isSnapshot) "snapshots" else "releases")
    Some(repo(repoName))
  }


  val creds = credentials += {
    Seq("build.publish.user", "build.publish.password").map(k => Option(System.getProperty(k))) match {
      case Seq(Some(user), Some(pass)) =>
        Credentials("Sonatype Nexus Repository Manager", "dev-iesl.cs.umass.edu", user, pass)
      case _ =>
        Credentials(Path.userHome / ".ivy2" / ".credentials")
    }
  }


  val scalaSettings = Seq(
    scalaVersion := scalaV,
    scalacOptions := Seq("-deprecation", "-unchecked", "-Xcheckinit", "-encoding", "utf8"),
    javacOptions ++= Seq("-Xlint:unchecked")
  )

  /*
  val otherRepos = Seq(
                             ScalaToolsSnapshots,
                             "Typesafe repo"     at "http://repo.typesafe.com/typesafe/releases/",
                             "IESL Repo"         at "https://dev-iesl.cs.umass.edu/nexus/content/groups/public/",
                             "spray repo"        at "http://repo.spray.cc/",
                             "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"
                           )
  */

  /*
    object V {
      val akka = "1.3"
      val spray = "0.9.0-RC1"
      val sprayJson = "1.1.0"
      val specs2 = "1.7.1"
      val jetty = "8.1.0.v20120127"
      val slf4j = "1.6.4"
      val logback = "1.0.6"
      val lift = "2.4"
      val h2 = "1.3.146"
      val dispatch = "0.8.7"
      val shiro = "1.3.0-SNAPSHOT"
      // apache shiro security project
      val casbah = "2.1.5.0"
      val jasypt = "1.9.0"
      val unfiltered = "0.6.1"
      val scalate = "1.5.3"
      val camel = "2.9.2"
      val activeMQ = "5.6.0"
    }
  */

  def scalaCompiler(v: String = scalaV) = "org.scala-lang" % "scala-compiler" % v

  def ieslScalaCommons(v: String = "latest.release") = "edu.umass.cs.iesl" %% "scalacommons" % v notTransitive() //  exclude("com.davidsoergel", "dsutils")

  def dsutils(v: String = "latest.release") = "com.davidsoergel" % "dsutils" % "1.04-SNAPSHOT" exclude("commons-logging", "commons-logging")

  def classutil(v: String = "latest.release") = "org.clapper" %% "classutil" % v

  def slf4s(v: String = "latest.release") = "com.weiglewilczek.slf4s" %% "slf4s" % v //"slf4s_2.9.1"

  def scalaIoCore(v: String = "latest.release") = "com.github.scala-incubator.io" %% "scala-io-core" % "0.4.0"

  def scalaIoFile(v: String = "latest.release") = "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.0"

  def akkaActor(v: String = "latest.release") = "se.scalablesolutions.akka" % "akka-actor" % v

  def akkaSlf4j(v: String = "latest.release") = "se.scalablesolutions.akka" % "akka-slf4j" % v

  def akkaRemote(v: String = "latest.release") = "se.scalablesolutions.akka" % "akka-remote" % v

  def boxterBrown(v: String = "latest.release") = "cc.acs" %% "boxter-brown" % "0.1-SNAPSHOT"

  def casbahLib(s: String)(v: String = "latest.release") = "com.mongodb.casbah" % ("casbah-" + s + "_2.9.0-1") % v

  def casbahLibs(v: String = "latest.release") = "core commons query".split(" ").toSeq map (l => casbahLib(l)(v))

  def commonsIo(v: String = "latest.release") = "commons-io" % "commons-io" % v

  def dispatchCore(v: String = "latest.release") = "net.databinder" %% "dispatch-core" % v

  def dispatchHttp(v: String = "latest.release") = "net.databinder" %% "dispatch-http" % v

  def dispatchNio(v: String = "latest.release") = "net.databinder" %% "dispatch-nio" % v

  def dispatchMime(v: String = "latest.release") = "net.databinder" %% "dispatch-mime" % v

  def dispatchJson(v: String = "latest.release") = "net.databinder" %% "dispatch-json" % v

  def h2(v: String = "latest.release") = "com.h2database" % "h2" % v % "compile"

  def hibem(v: String = "latest.release") = "org.hibernate" % "hibernate-entitymanager" % v % "compile"

  def hibval(v: String = "latest.release") = "org.hibernate" % "hibernate-validator-annotation-processor" % v % "compile"

  // java simplified encryption
  def jasypt(v: String = "latest.release") = "org.jasypt" % "jasypt" % v

  def jdom(v: String = "latest.release") = "org.jdom" % "jdom" % v

  def mavenCobertura(v: String = "latest.release") = "org.codehaus.mojo" % "cobertura-maven-plugin" % v % "test"

  def mavenFindbugs(v: String = "latest.release") = "org.codehaus.mojo" % "findbugs-maven-plugin" % v % "test"

  def jaxen(v: String = "latest.release") = (("jaxen" % "jaxen" % v notTransitive())
    .exclude("maven-plugins", "maven-cobertura-plugin")
    .exclude("maven-plugins", "maven-findbugs-plugin")
    .exclude("dom4j", "dom4j")
    .exclude("jdom", "jdom")
    .exclude("xml-apis", "xml-apis")
    .exclude("xerces", "xercesImpl")
    .exclude("xom", "xom"))

  def jettison(v: String = "latest.release") = "org.codehaus.jettison" % "jettison" % v

  def jwebunit(v: String = "latest.release") = "net.sourceforge.jwebunit" % "jwebunit-htmlunit-plugin" % v

  def liftAmqp(v: String = "latest.release") = "net.liftweb" %% "lift-amqp" % v

  def liftJson(v: String = "latest.release") = "net.liftweb" %% "lift-json" % v

  def liftCouch(v: String = "latest.release") = "net.liftweb" %% "lift-couchdb" % v

  def liftJpa(v: String = "latest.release") = "net.liftweb" %% "lift-jpa" % v

  def liftJta(v: String = "latest.release") = "net.liftweb" %% "lift-jta" % v

  def liftMachine(v: String = "latest.release") = "net.liftweb" %% "lift-machine" % v

  def liftMapper(v: String = "latest.release") = "net.liftweb" %% "lift-mapper" % v

  def liftMongo(v: String = "latest.release") = "net.liftweb" %% "lift-mongodb-record" % v

  def liftPaypal(v: String = "latest.release") = "net.liftweb" %% "lift-paypal" % v

  def liftScalate(v: String = "latest.release") = "net.liftweb" %% "lift-scalate" % v

  def liftSqueryl(v: String = "latest.release") = "net.liftweb" %% "lift-squeryl-record" % v

  def liftTestkit(v: String = "latest.release") = "net.liftweb" %% "lift-testkit" % v

  def liftTextile(v: String = "latest.release") = "net.liftweb" %% "lift-textile" % v

  def liftWebkit(v: String = "latest.release") = "net.liftweb" %% "lift-webkit" % v

  def liftWidgets(v: String = "latest.release") = "net.liftweb" %% "lift-widgets" % v

  def liftWizard(v: String = "latest.release") = "net.liftweb" %% "lift-wizard" % v

  def mongodb(v: String = "latest.release") = "org.mongodb" % "mongo-java-driver" % v

  def mysql(v: String = "latest.release") = "mysql" % "mysql-connector-java" % v

  def neo4j(v: String = "latest.release") = "org.neo4j" % "neo4j" % v

  def ostrich(v: String = "latest.release") = "com.twitter" % "ostrich" % v

  //"9.0-801.jdbc4"
  def postgresql(v: String = "latest.release") = "postgresql" % "postgresql" % v

  def redstoneXMLRPC(v: String = "latest.release") = "org.kohsuke.redstone" % "redstone" % v

  def scalateCore(v: String = "latest.release") = "org.fusesource.scalate" % "scalate-core" % v

  def scalazCore(v: String = "latest.release") = "org.scalaz" %% "scalaz-core" % v

  def scalaQuery(v: String = "latest.release") = "org.scalaquery" %% "scalaquery" % v

  def selenium(v: String = "latest.release") = "org.seleniumhq.selenium" % "selenium-java" % v

  def seleniumsvr(v: String = "latest.release") = "org.seleniumhq.selenium" % "selenium-server" % v

  def shiroCore(v: String = "latest.release") = "org.apache.shiro" % "shiro-core" % v

  def shiroWeb(v: String = "latest.release") = "org.apache.shiro" % "shiro-web" % v

  def shiroEHCache(v: String = "latest.release") = "org.apache.shiro" % "shiro-ehcache" % v

  def apacheCommonsLang3(v: String = "latest.release") = "org.apache.commons" % "commons-lang3" % v

  def apacheCommonsEmail(v: String = "latest.release") = "org.apache.commons" % "commons-email" % v

  def specs(v: String = "latest.release") = "org.scala-tools.testing" %% "specs" % v

  def sprayJson(v: String = "latest.release") = "cc.spray" %% "spray-json" % v

  def sprayServer(v: String = "latest.release") = "cc.spray" % "spray-server" % v

  def slf4j(v: String = "latest.release") = "org.slf4j" % "slf4j-api" % v

  def subcut(v: String = "latest.release") = "org.scala-tools.subcut" %% "subcut" % v

  def jacksonCore(v: String = "latest.release") = "org.codehaus.jackson" % "jackson-core-asl" % v

  def bson4jackson(v: String = "latest.release") = "de.undercouch" % "bson4jackson" % v

  def jerkson(v: String = "latest.release") = "com.codahale" %% "jerkson" % v

  def unfiltered(v: String = "latest.release") = "net.databinder" %% "unfiltered" % v

  def unfilteredScalatest(v: String = "latest.release") = "net.databinder" %% "unfiltered-scalatest" % v

  def unfilteredSpec(v: String = "latest.release") = "net.databinder" %% "unfiltered-spec" % v

  def unfilteredFilter(v: String = "latest.release") = "net.databinder" %% "unfiltered-filter" % v

  def unfilteredJetty(v: String = "latest.release") = "net.databinder" %% "unfiltered-jetty" % v

  def unfilteredNetty(v: String = "latest.release") = "net.databinder" %% "unfiltered-netty" % v

  def unfilteredUploads(v: String = "latest.release") = "net.databinder" %% "unfiltered-uploads" % v

  def activeMQ(v: String = "latest.release") = "org.apache.activemq" % "activemq-core" % v

  def activeMQCamel(v: String = "latest.release") = "org.apache.activemq" % "activemq-camel" % v

  def activeMQPool(v: String = "latest.release") = "org.apache.activemq" % "activemq-pool" % v


  def camel(components: String*)(v: String = "latest.release"): Seq[ModuleID] = {
    val compNames = ("ahc,amqp,apns,atom,aws,bam,bean-validator,bindy,blueprint,buildtools,bundle,cache,castor,cometd,context,core,core-osgi,core-xml,crypto,csv,cxf,cxf-transport,dns,dozer,eclipse,ejb,eventadmin,exec,flatpack,freemarker,ftp,gae,groovy,guice,hamcrest,hawtdb,hazelcast,hdfs,hl7,http,http4,ibatis,irc,itest,itest-osgi,itest-spring-2.0,itest-spring-2.5,itest-standalone,jackson,jasypt,javaspace,jaxb,jclouds,jcr,jdbc,jdbc-aggregator,jetty,jhc,jibx,jing,jms,jmx,josql,jpa,jsch,jt400,juel,jxpath,kestrel,krati,ldap,lucene,mail,manual,maven-plugin,mina,mina2,msv,mvel,mybatis,nagios,netty,ognl,osgi,parent,partial-classpath-test,paxlogging,printer,protobuf,quartz,quickfix,rest,restlet,rmi,routebox,rss,ruby,saxon,scala,script,servlet,shiro,sip,smpp,snmp,soap,solr,spring,spring-integration,spring-javaconfig,spring-osgi,spring-security,spring-ws,sql,stax,stream,stringtemplate,swing,syslog,tagsoup,test,testng,typeconverterscan-test,uface,velocity,web,web-standalone,xmlbeans,xmlsecurity,xmpp,xstream,zookeeper"
      .split(",")
      .toSeq);

    def dep(c: String) = "org.apache.camel" % ("camel-" + c) % v

    for {
      c <- components
    } yield {
      assert(compNames.contains(c))
      dep(c)
    }
  }


  def junit4(v: String = "latest.release") = "junit" % "junit" % v % "test"

  def specs2(v: String = "latest.release") = "org.specs2" %% "specs2" % v % "test"

  def scalacheck(v: String = "latest.release") = "org.scala-tools.testing" %% "scalacheck" % v % "test"

  def scalatest(v: String = "latest.release") = "org.scalatest" %% "scalatest" % v % "test"

  def jettyWebApp(v: String = "latest.release") = "org.eclipse.jetty" % "jetty-webapp" % v % "container"

  def logbackClassic(v: String = "latest.release") = "ch.qos.logback" % "logback-classic" % v

  def logbackCore(v: String = "latest.release") = "ch.qos.logback" % "logback-core" % v

  def servletApi(v: String = "latest.release") = "javax.servlet" % "servlet-api" % v % "provided"


}



