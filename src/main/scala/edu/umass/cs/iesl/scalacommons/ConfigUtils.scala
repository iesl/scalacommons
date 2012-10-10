package edu.umass.cs.iesl.scalacommons

trait ConfigUtils {
  import scala.collection.JavaConverters._

  import com.typesafe.config.{ 
    Config, 
    ConfigFactory, 
    ConfigParseOptions, 
    ConfigSyntax, 
    ConfigOrigin, 
    ConfigException 
  }

  val propre = """\{\{(.*)\}\}""".r;

  def propSub(conf:Config, s:String): String = {
    val matches = propre.findAllIn(s)
    matches.matchData.toList.foldLeft(s) {
      case (acc, m) => {
        val before = acc.slice(0, m.start)
        val after = acc.slice(m.end, acc.length)
        val propname = acc.slice(m.start+2, m.end-2).trim
        before + conf.getString(propname) + after
      }
    }
  }

  /**
   * same function as above, but a bit terser and more amenable to implicit configs 
   */
  def psub(s:String)(implicit conf:Config): String = propSub(conf, s)

  def prop(propname:String)(implicit conf:Config): String = 
    conf.getString(propname)


  /**
   * Build URIs using a more readable syntax,
   * one line per parameter, with property substitution, like so:
   *
   * Given property definitions:
   *   file.name=/path/to/file
   *   delay.value=1000
   *
   * Then
   *    mkUri("""|file:{{file.name}}
   *             |  initialDelay={{delay.value}}
   *             |  delay=1000
   *          """)
   * becomes
   *    "file://path/to/file?initialDelay=1000&delay=1000"
   *
   */
  def mkUri(uristr:String)(implicit conf:Config): String = {
    val lines = uristr.trim.stripMargin.split("\n").toList.map(propSub(conf, _).trim)
    lines.head + "?" + lines.tail.mkString("&")
  }

  def configFromMap(map: Map[String, Any]): Config = {
    ConfigFactory.parseMap(map.asJava)
  }

  def empty = ConfigFactory.empty 

  def load(confname: String): Either[Exception, Config] = {
    try {
      Right(ConfigFactory.load(confname))
    } catch {
      case e:Exception => Left(e)
    }
  }

}

object ConfigUtils extends ConfigUtils
