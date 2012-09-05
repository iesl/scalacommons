package edu.umass.cs.iesl.scalacommons

import com.weiglewilczek.slf4s.Logging
import collection.{GenIterable, GenTraversable}

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
  // but that had serious issues!  Rewritten...

  def argMax[A, B: Ordering](input: GenIterable[A], f: A => B) = argMaxZip(input, f) map (_._1) toSet

  def argMaxZip[A, B: Ordering](input: GenIterable[A], f: A => B): GenIterable[(A, B)] = {
    if (input.isEmpty) Nil
    else {
      val fPairs = input map (x => (x, f(x)))
      val maxF = fPairs.map(_._2).max
      fPairs filter (_._2 == maxF)
    }
  }

  // trouble using Ordering.reverse, so just cut and paste for now

  def argMin[A, B: Ordering](input: GenIterable[A], f: A => B) = argMinZip(input, f) map (_._1) toSet


  def argMinZip[A, B: Ordering](input: GenIterable[A], f: A => B): GenIterable[(A, B)] = {
    if (input.isEmpty) Nil
    else {
      val fPairs = input map (x => (x, f(x)))
      val minF = fPairs.map(_._2).min
      fPairs filter (_._2 == minF)
    }
  }


  // could do a version that produces (B, Iterable[A]]
}

class SeqMergeException[T](x: T, y: T) extends Exception("unequal sequences: " + x + "  ,  " + y)
