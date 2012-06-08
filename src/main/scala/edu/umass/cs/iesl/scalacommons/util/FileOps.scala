package edu.umass.cs.iesl.scalacommons
package util

object FileOps {

  import java.io._
  def file(s:String) = new File(s)  
  def file(d:File, s:String) = new File(d, s)
  def fistream(f:java.io.File) = new FileInputStream(f)
  def fistream(s:String) = new FileInputStream(file(s))
  def reader(is:InputStream) = new InputStreamReader(is)

  import scala.io.Source
  import scala.io.BufferedSource

  def getResource[A](cls:Class[A], path:String) = cls.getResource(path)
  def getResourceStream[A](cls:Class[A], path:String) = cls.getResourceAsStream(path)
  def getResourceSource[A](cls:Class[A], path:String) = scala.io.Source.fromInputStream(getResourceStream(cls, path))
  def getResourceFile[A](cls:Class[A], path:String) = new java.io.File(getResource(cls, path).toURI())

}
