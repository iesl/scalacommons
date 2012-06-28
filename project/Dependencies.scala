import sbt._
// import com.github.siasia._
// import WebPlugin._
// import PluginKeys._
// import Keys._

object Dependencies {

val resolutionRepos = Seq(
	                         ScalaToolsSnapshots,
	                         "Typesafe repo"     at "http://repo.typesafe.com/typesafe/releases/",
	                         "IESL Repo"         at "https://dev-iesl.cs.umass.edu/nexus/content/groups/public/",
	                         "spray repo"        at "http://repo.spray.cc/",
	                         "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"
                         )

object V {
val akka        = "1.3"
val spray       = "0.9.0-RC1"
val sprayJson   = "1.1.0"
val specs2      = "1.7.1"
val jetty       = "8.1.0.v20120127"
val slf4j       = "1.6.4"
val logback     = "1.0.6"
val lift        = "2.4"
val h2          = "1.3.146"
val dispatch    = "0.8.7"
val shiro       = "1.3.0-SNAPSHOT"  // apache shiro security project
val casbah      = "2.1.5.0"
val jasypt      = "1.9.0"
val unfiltered  = "0.6.1"
val scalate     = "1.5.3"
val camel       = "2.9.2"
val activeMQ    = "5.6.0"
}



val ieslScalaCommons      = "edu.umass.cs.iesl"         %% "scalacommons"              % "0.1-SNAPSHOT" notTransitive() //  exclude("com.davidsoergel", "dsutils")

val akkaActor             = "se.scalablesolutions.akka" %  "akka-actor"                % V.akka
val akkaSlf4j             = "se.scalablesolutions.akka" %  "akka-slf4j"                % V.akka
val akkaRemote            = "se.scalablesolutions.akka" %  "akka-remote"               % V.akka
val boxterBrown           = "cc.acs"                    %% "boxter-brown"              % "0.1-SNAPSHOT"
def casbahLib(s:String)   = "com.mongodb.casbah"     % ("casbah-" + s + "_2.9.0-1") % V.casbah
val casbahLibs            = "core commons query".split(" ").toSeq map (l => casbahLib(l))
val commonsIo             = "commons-io"                %  "commons-io"                % "2.0.1"
val dispatchCore          = "net.databinder"            %% "dispatch-core"             % V.dispatch
val dispatchHttp          = "net.databinder"            %% "dispatch-http"             % V.dispatch
val dispatchNio           = "net.databinder"            %% "dispatch-nio"              % V.dispatch
val dispatchMime          = "net.databinder"            %% "dispatch-mime"             % V.dispatch
val dispatchJson          = "net.databinder"            %% "dispatch-json"             % V.dispatch
val h2                    = "com.h2database"            %  "h2"                        % V.h2                         % "compile"
val hibem                 = "org.hibernate"             %  "hibernate-entitymanager"   % "3.6.0.Final"                % "compile"
val hibval                = "org.hibernate"             %  "hibernate-validator-annotation-processor" % "4.1.0.Final" % "compile"
val jasypt                = "org.jasypt"                % "jasypt"                     % V.jasypt // java simplified encryption
val jdom                  = "org.jdom"                  %  "jdom"                      % "1.1"
// val jaxen                 = "jaxen"                     %  "jaxen"                     % "1.1.1"

val mavenCobertura = "org.codehaus.mojo" % "cobertura-maven-plugin" % "2.5.1"

val mavenFindbugs = "org.codehaus.mojo" % "findbugs-maven-plugin" % "2.3.3"

val jaxen                 = (("jaxen"                            %    "jaxen"                    %   "1.1.3" notTransitive())
                             .exclude("maven-plugins", "maven-cobertura-plugin")
                             .exclude("maven-plugins", "maven-findbugs-plugin")
                             .exclude("dom4j", "dom4j")
                             .exclude("jdom", "jdom")
                             .exclude("xml-apis", "xml-apis")
                             .exclude("xerces", "xercesImpl")
                             .exclude("xom", "xom"))

val jettison              = "org.codehaus.jettison"     %  "jettison"                  % "1.3"
val jwebunit              = "net.sourceforge.jwebunit"  %  "jwebunit-htmlunit-plugin"  % "2.5"
val liftAmqp              = "net.liftweb"               %% "lift-amqp"                 % V.lift
val liftJson              = "net.liftweb"               %% "lift-json"                 % V.lift
val liftCouch             = "net.liftweb"               %% "lift-couchdb"              % V.lift
val liftJpa               = "net.liftweb"               %% "lift-jpa"                  % V.lift
val liftJta               = "net.liftweb"               %% "lift-jta"                  % V.lift
val liftMachine           = "net.liftweb"               %% "lift-machine"              % V.lift
val liftMapper            = "net.liftweb"               %% "lift-mapper"               % V.lift
val liftMongo             = "net.liftweb"               %% "lift-mongodb-record"       % V.lift
val liftPaypal            = "net.liftweb"               %% "lift-paypal"               % V.lift
val liftScalate           = "net.liftweb"               %% "lift-scalate"              % V.lift
val liftSqueryl           = "net.liftweb"               %% "lift-squeryl-record"       % V.lift
val liftTestkit           = "net.liftweb"               %% "lift-testkit"              % V.lift
val liftTextile           = "net.liftweb"               %% "lift-textile"              % V.lift
val liftWebkit            = "net.liftweb"               %% "lift-webkit"               % V.lift
val liftWidgets           = "net.liftweb"               %% "lift-widgets"              % V.lift
val liftWizard            = "net.liftweb"               %% "lift-wizard"               % V.lift
val mongodb               = "org.mongodb"               % "mongo-java-driver"          % "2.7.3"
val mysql                 = "mysql"                     % "mysql-connector-java"       % "5.1.18"
val neo4j                 = "org.neo4j"                 %  "neo4j"                     % "1.6"
val ostrich               = "com.twitter"               % "ostrich"                    % "4.1.0"
val postgresql            = "postgresql"                % "postgresql"                 % "9.0-801.jdbc4"
val redstoneXMLRPC        = "org.kohsuke.redstone"      % "redstone"                   % "1.1.1"
val scalateCore           = "org.fusesource.scalate"    % "scalate-core"               % V.scalate
val scalazCore            = "org.scalaz"                %% "scalaz-core"               % "6.0.4"
val scalaQuery            = "org.scalaquery"            %% "scalaquery"                % "0.10.0-M1"
val selenium              = "org.seleniumhq.selenium"   % "selenium-java"              % "2.3.1"
val seleniumsvr           = "org.seleniumhq.selenium"   % "selenium-server"            % "2.3.1"
val shiroCore             = "org.apache.shiro"          % "shiro-core"                 % V.shiro
val shiroWeb              = "org.apache.shiro"          % "shiro-web"                  % V.shiro
val shiroEHCache          = "org.apache.shiro"          % "shiro-ehcache"              % V.shiro

object apache {
object commons {
val lang3          = "org.apache.commons"          % "commons-lang3"              % "3.1"
val email          = "org.apache.commons"          % "commons-email"              % "1.2"
}
}

val specs                 = "org.scala-tools.testing"   %% "specs"                     % "1.6.7.2"
val sprayJson             = "cc.spray"               %% "spray-json"                   % V.sprayJson
val sprayServer           = "cc.spray"               %  "spray-server"                 % V.spray
val slf4j                 = "org.slf4j"              %  "slf4j-api"                    % V.slf4j
val subcut                = "org.scala-tools.subcut" %% "subcut"                       % "1.0"
val jacksonCore           = "org.codehaus.jackson"   % "jackson-core-asl"              % "1.9.4"
val bson4jackson          = "de.undercouch"          % "bson4jackson"                  % "1.3.0"
val jerkson               = "com.codahale"           %% "jerkson"                      % "0.5.0"
val unfiltered            = "net.databinder"         %% "unfiltered"                   % V.unfiltered
val unfilteredScalatest   = "net.databinder"         %% "unfiltered-scalatest"         % V.unfiltered
val unfilteredSpec        = "net.databinder"         %% "unfiltered-spec"              % V.unfiltered
val unfilteredFilter      = "net.databinder"         %% "unfiltered-filter"            % V.unfiltered
val unfilteredJetty       = "net.databinder"         %% "unfiltered-jetty"             % V.unfiltered
val unfilteredNetty       = "net.databinder"         %% "unfiltered-netty"             % V.unfiltered
val unfilteredUploads     = "net.databinder"         %% "unfiltered-uploads"           % V.unfiltered

val activeMQ              = "org.apache.activemq"    %  "activemq-core"                % V.activeMQ
val activeMQCamel         = "org.apache.activemq"    %  "activemq-camel"               % V.activeMQ
val activeMQPool          = "org.apache.activemq"    %  "activemq-pool"                % V.activeMQ


def camel(components: String*): Seq[ModuleID] = {
val compNames = ("ahc,amqp,apns,atom,aws,bam,bean-validator,bindy,blueprint,buildtools,bundle,cache,castor,cometd,context,core,core-osgi,core-xml,crypto,csv,cxf,cxf-transport,dns,dozer,eclipse,ejb,eventadmin,exec,flatpack,freemarker,ftp,gae,groovy,guice,hamcrest,hawtdb,hazelcast,hdfs,hl7,http,http4,ibatis,irc,itest,itest-osgi,itest-spring-2.0,itest-spring-2.5,itest-standalone,jackson,jasypt,javaspace,jaxb,jclouds,jcr,jdbc,jdbc-aggregator,jetty,jhc,jibx,jing,jms,jmx,josql,jpa,jsch,jt400,juel,jxpath,kestrel,krati,ldap,lucene,mail,manual,maven-plugin,mina,mina2,msv,mvel,mybatis,nagios,netty,ognl,osgi,parent,partial-classpath-test,paxlogging,printer,protobuf,quartz,quickfix,rest,restlet,rmi,routebox,rss,ruby,saxon,scala,script,servlet,shiro,sip,smpp,snmp,soap,solr,spring,spring-integration,spring-javaconfig,spring-osgi,spring-security,spring-ws,sql,stax,stream,stringtemplate,swing,syslog,tagsoup,test,testng,typeconverterscan-test,uface,velocity,web,web-standalone,xmlbeans,xmlsecurity,xmpp,xstream,zookeeper"
                 .split(",")
                 .toSeq);

def dep(c:String) = "org.apache.camel" % ("camel-"+c) % V.camel

for {
	c <- components
} yield {
assert(compNames.contains(c))
dep(c)
}
}


val junit4       = "junit"                     %  "junit"                % "4.10"    % "test"
val specs2       = "org.specs2"                %% "specs2"               % V.specs2  % "test"
val scalacheck   = "org.scala-tools.testing"   %% "scalacheck"           % "1.8"     % "test"
val scalatest    = "org.scalatest"             % "scalatest"             % "1.3"     % "test"


val jettyWebApp    = "org.eclipse.jetty"         %  "jetty-webapp"    % V.jetty   % "container"
val logbackClassic = "ch.qos.logback"            %  "logback-classic" % V.logback
val logbackCore    = "ch.qos.logback"            %  "logback-core"    % V.logback
val servletApi     = "javax.servlet"             %  "servlet-api"     %  "2.5"    %  "provided"


}



