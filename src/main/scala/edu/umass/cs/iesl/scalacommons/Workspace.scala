package edu.umass.cs.iesl.scalacommons

import scalax.io._
import tools.nsc.io._
import java.io.InputStream
import com.typesafe.scalalogging.{StrictLogging => Logging}
import scala.util.Random

/**
 * a File to operate on together with a temp directory.
 */
trait Workspace {
  def filename: String

  def file: File

  def dir: Directory

  def clean()
}

object TempDirFactory {
  // this just recapitulates Directory.makeTemp, except that deleteOnExit can be disabled
  def apply(): Directory = {
    val jfile = java.io.File.createTempFile(Random.alphanumeric take 6 mkString, null, null)
    // jfile.deleteOnExit()
    val path = new File(jfile)
    path.delete()
    path.createDirectory()
  }
}

class StreamWorkspace(val filename: String, instream: InputStream) extends Workspace with Logging {
  val (dir, file) = {
    val d = TempDirFactory() //Directory.makeTemp()
    val f = File(d + File.separator + filename)
    val outstream = f bufferedOutput (false)
    Resource.fromInputStream(instream) copyDataTo Resource.fromOutputStream(outstream)
    instream.close()
    outstream.close()

    logger.debug("Created StreamWorkspace in " + d + " containing " + f)
    (d, f)
  }

  //lazy val file = File(dir + File.separator + filename)

  def clean() {
    dir deleteRecursively()
  }
}


class FileWorkspace(val jfile: java.io.File) extends Workspace with Logging {
  val file = File(jfile)
  val dir = {
    val d = TempDirFactory() //Directory.makeTemp()
    logger.debug(s"Created FileWorkspace in $d for $file  ${d.exists}")
    d
  }

  val filename = file.name
  //lazy val file = File(dir + File.separator + filename)
  def clean() {
    dir deleteRecursively()
  }
}
