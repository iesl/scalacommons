package edu.umass.cs.iesl.scalacommons

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */

import java.io.File
import sys.SystemProperties
import java.util.NoSuchElementException
import org.clapper.classutil.{ClassInfo, ClassFinder}
import com.weiglewilczek.slf4s.Logging
import collection.immutable.List

// originally informed by from http://thoughts.inphina.com/2011/09/15/building-a-plugin-based-architecture-in-scala/
/**
 * Manages a set of plugins, which must be objects.
 * B is a base type from which all plugins must inherit.  This just makes classpath scanning more efficient; use Any if you don't care
 */
class PluginManager[B <: PluginManager.HasName](implicit man: Manifest[B]) extends Logging {

  private val pluginBaseName = man.toString

  lazy val allClasses: Map[String, ClassInfo] = {

    lazy val urls = java.lang.Thread.currentThread.getContextClassLoader match {
      case cl: java.net.URLClassLoader => cl.getURLs.toList
      case _ => error("classloader is not a URLClassLoader")
    }


    lazy val classpath = {
      val r : Seq[File] = urls map (_.toURI) map (new File(_))
      //logger.info("LOCAL CLASSPATH: " + r.map(_.toString).mkString("\n"))

      val x = (new SystemProperties)("java.class.path").split(";").map(new File(_)).toSeq
      //logger.info("SYSTEM CLASSPATH: " + x.map(_.toString).mkString("\n"))

      val xxx = r ++ x
      val all = xxx.toSet[File]
      all
    }

    val extraClasses = {
      try {
        val pluginClasspath = (new SystemProperties)("PluginClasspath")
        if (pluginClasspath != null && !pluginClasspath.isEmpty()) {
          val classpath = pluginClasspath.split(";").map(new File(_))

          //logger.info("PLUGIN CLASSPATH: " + classpath.map(_.toString).mkString("\n"))

          val finder = ClassFinder(classpath)
          finder.getClasses
        } else List[ClassInfo]()
      } catch {
        case e: NoSuchElementException => List[ClassInfo]()
      }
    }

    val classes = ClassFinder(classpath.toSeq).getClasses() ++ extraClasses
    val allClassMap = ClassFinder.classInfoMap(classes)
    allClassMap
  }

  private lazy val filteredClasses = {
    //logger.warn("Finding " + pluginBaseName + " in classpath: ")

    val x = ClassFinder.concreteSubclasses(pluginBaseName, allClasses) ++ allClasses.get(pluginBaseName)


    //logger.warn(x.map(_.toString).mkString("\n"))

    val result = x.map((ci) => {
      val n: String = ci.name
      (n -> ci)
    }).toMap


    //logger.warn(result.map(_.toString()).mkString("\n"))
    result

  }


  def findPlugins[T](implicit man: Manifest[T]): Map[String, T] = new SingleTypePluginManager[T].findPlugins(filteredClasses)

  //def findPlugins[T](): Map[String, T] = Map()

  private class SingleTypePluginManager[T](implicit man: Manifest[T]) {
    val baseTypeName = man.erasure.getName
    /**
     * Returns a map from plugin names to plugins.  If the plugin has a "name" member, it is used; otherwise the class name is used.
     * @param classes
     * @return
     */
    def findPlugins(classes: Map[String, ClassInfo]): Map[String, T] = {

      //logger.warn("Finding " + baseTypeName + " in classpath: ")
      val x = ClassFinder.concreteSubclasses(baseTypeName, classes ++ allClasses.get(baseTypeName).map(baseTypeName -> _))

      x.map((ci) => {
        val className: String = ci.name

        val comp: T = companion(className)
        //logger.warn("Found: " + className + " -> " + comp.toString)

        val pluginName = if (comp.isInstanceOf[PluginManager.HasName]) comp.asInstanceOf[PluginManager.HasName].name else className;
        (pluginName -> comp)
      }).toMap

    }

    private def companion(name: String): T =
      Class.forName(if (name.endsWith("$")) name else (name + "$")).getField("MODULE$").get(man.erasure).asInstanceOf[T]
  }

}

object PluginManager {

  type HasName = {
    def name: String
  }
}
