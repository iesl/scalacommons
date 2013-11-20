package edu.umass.cs.iesl.scalacommons

import scala.collection.{GenIterable, GenMap}

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 */
object MapUtils {
  //http://daily-scala.blogspot.com/2010/03/how-to-reverse-map.html
  def invert[A, B](x: GenMap[A, B]): GenMap[B, GenIterable[A]] = x groupBy {
    _._2
  } map {
    case (key, value) => (key, value.unzip._1)
  }
}
