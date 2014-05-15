package edu.umass.cs.iesl.scalacommons

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */

import java.io.File
import sys.SystemProperties
import java.util.NoSuchElementException
import org.clapper.classutil.{ClassInfo, ClassFinder}
import com.typesafe.scalalogging.slf4j.Logging
import collection.immutable.List
import tools.nsc.util.ScalaClassLoader.URLClassLoader
import scala.annotation.tailrec

// originally informed by from http://thoughts.inphina.com/2011/09/15/building-a-plugin-based-architecture-in-scala/
/**
 * Manages a set of plugins, which must be objects.
 * B is a base type from which all plugins must inherit.  This just makes classpath scanning more efficient; use Any if you don't care
 */
class PluginManager[B <: PluginManager.HasName](implicit man: Manifest[B]) extends Logging {

  private val pluginBaseName = man.toString

  val classpathFiles: Seq[File] = {
    val pluginClasspath = (new SystemProperties)("PluginClasspath")
    if (pluginClasspath != null && !pluginClasspath.isEmpty()) {
      pluginClasspath.split(";").map(new File(_))
    }
    else Nil
  }

  lazy val allClasses: Map[String, ClassInfo] = {
    lazy val urls = java.lang.Thread.currentThread.getContextClassLoader match {
      case cl: java.net.URLClassLoader => cl.getURLs.toList
      case _ => sys.error("classloader is not a URLClassLoader")
    }

    lazy val classpath = {
      val r: Seq[File] = urls map (_.toURI) map (new File(_))
      //logger.info("LOCAL CLASSPATH: " + r.map(_.toString).mkString("\n"))
      val x = (new SystemProperties)("java.class.path").split(";").map(new File(_)).toSeq
      //logger.info("SYSTEM CLASSPATH: " + x.map(_.toString).mkString("\n"))
      val xxx = r ++ x
      val all = xxx.toSet[File]
      all
    }

    val extraClasses = {
      try {
        if (!classpathFiles.isEmpty) {
          val finder = ClassFinder(classpathFiles)
          val asdf = finder.getClasses
          asdf
        }
        else Stream[ClassInfo]()
      } catch {
        case e: NoSuchElementException => Stream[ClassInfo]()
      }
    }

    val classes = ClassFinder(classpath.toSeq).getClasses() ++ extraClasses
    val allClassMap = ClassFinder.classInfoMap(classes.toIterator)
    allClassMap
  }

  /*
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
  */

  def findPlugins[T](implicit man: Manifest[T]): Map[String, T] = new SingleTypePluginManager[T].findPlugins

  def concreteSubclasses(ancestor: String, classes: Map[String, ClassInfo]): Iterable[ClassInfo] = allSubclasses(ancestor,classes.values).filter(_.isConcrete)
  
  //@tailrec
  def allSubclasses(ancestor: String, classes:Iterable[ClassInfo]): Iterable[ClassInfo] = {
    val direct = classes.filter(x => x.superClassName == ancestor || x.implements(ancestor))
    val indirect = direct.flatMap(x=>allSubclasses(x.name,classes))
    direct ++ indirect
  }
  //(filteredClasses)

  //def findPlugins[T](): Map[String, T] = Map()
  private class SingleTypePluginManager[T](implicit man: Manifest[T]) {
    val baseTypeName = man.runtimeClass.getName

    /**
     * Returns a map from plugin names to plugins.  If the plugin has a "name" member, it is used; otherwise the class name is used.
     * @param classes
     * @return
     */
    val findPlugins: Map[String, T] = {
      //(classes: Map[String, ClassInfo])

      //logger.warn("Finding " + baseTypeName + " in classpath: ")
      /*
      val x = allClasses.collect({
        case (n, ci) if ci.interfaces.contains(baseTypeName) && ci.isConcrete => ci
      })
      */

      //val x = ClassFinder.concreteSubclasses(baseTypeName, classes ++ allClasses.get(baseTypeName).map(baseTypeName -> _)).toList

      // horribly inefficient because it doesn't memoize properly
      //val x = ClassFinder.concreteSubclasses(baseTypeName, allClasses).toList
      
      val x = concreteSubclasses(baseTypeName, allClasses)

      x.map((ci) => {
        val className: String = ci.name

        val compo: Option[T] = companion(className)
        val result = compo map {
          comp =>
          //logger.warn("Found: " + className + " -> " + comp.toString)
           // val pluginName = if (comp.isInstanceOf[PluginManager.HasName]) comp.asInstanceOf[PluginManager.HasName].name else className
          
          val pluginName = try { comp.asInstanceOf[PluginManager.HasName].name} catch {case e: NoSuchMethodException => className}
          
            (pluginName -> comp)
        }
        result
      }).flatten.toMap
    }

    class Bogus {}

    lazy val classloader = new URLClassLoader(classpathFiles.map(_.toURI.toURL), classOf[Bogus].getClassLoader)
    //logger.warn("Built classloader: " + classloader.getURLs.mkString("\n"))

    private def companion(name: String): Option[T] = {
      val classname: String = if (name.endsWith("$")) name else (name + "$")
      //logger.warn("Loading class " + classname)

      try {
        Some(classloader.loadClass(classname).getField("MODULE$").get(man.runtimeClass).asInstanceOf[T])
      }
      catch {
        case e: ClassNotFoundException => None
      }
    }
  }

}

object PluginManager {
  type HasName = {def name: String}
}
