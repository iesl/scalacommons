package edu.umass.cs.iesl.scalacommons

import com.weiglewilczek.slf4s.Logging
import collection.GenTraversable

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */
object SeqUtils extends Logging {

  implicit def emptyCollectionToNone[T <: GenTraversable[Any]](s: T): Option[T] = if (s.isEmpty) None else Some(s)

  def mergeWarn[T, This <: GenTraversable[T]](a: This, b: This): This = {
    (a, b) match {
      case (Nil, Nil) => a
      case (p, Nil) => a
      case (Nil, q) => b
      case (p, q) => {
        if (p != q) {
          logger.warn("Merging unequal sequences, preferring: " + p + "  to " + q)
        }
        a
      }
    }
  }

  def mergeOrFail[T, This <: GenTraversable[T]](a: This, b: This): This = {
    (a, b) match {
      case (Nil, Nil) => a
      case (p, Nil) => a
      case (Nil, q) => b
      case (p, q) => {
        if (p != q) {
          throw new SeqMergeException(p, q)
        }
        a
      }
    }
  }


  // based on Daniel Sobral.  http://stackoverflow.com/questions/3050557/how-can-i-extend-scala-collections-with-an-argmax-method
  def argMax[A, B: Ordering](input: Iterable[A], f: A => B) = argMaxZip(input, f) map (_._1) toSet

  // compact version

  def argMaxZip[A, B: Ordering](input: Iterable[A], f: A => B): Iterable[(A, B)] = {
    val fList = input map f
    val maxFList = fList.max
    input.view zip fList filter (_._2 == maxFList)
  }

  // could do a version that produces (B, Iterable[A]]

}

class SeqMergeException[T](x: T, y: T) extends Exception("unequal sequences: " + x + "  ,  " + y)
