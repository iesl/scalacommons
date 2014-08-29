package edu.umass.cs.iesl.scalacommons

import com.typesafe.scalalogging.{StrictLogging => Logging}
import com.typesafe.scalalogging.Logger


// http://stackoverflow.com/questions/17571800/avoiding-the-variable-in-val-x-foo-barx-x

object Tap {
  implicit def toTappable[A](value: A): Tap[A] = new Tap(value)
}

class Tap[A](value: A) {

  def tap(f: A => Any): A = {
    f(value)
    value
  }

  def debug(prefix: String)(implicit logger: Logger): A = {
    logger.debug(prefix + value)
    value
  }
}

object TapExample extends Logging {

  import Tap._

  implicit val loggerI = logger

  val c = 2 + 2 tap {
    x => logger.debug("The sum is: " + x)
  }

  val d = 2 + 2 debug "The sum is: "

  assert(d == 4)
}

