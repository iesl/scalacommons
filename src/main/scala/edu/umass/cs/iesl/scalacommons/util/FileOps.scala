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

  def reSource[A](cls:Class[A], path:String): BufferedSource = Source.fromURL(cls.getResource(path))

}
