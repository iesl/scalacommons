package edu.umass.cs.iesl.scalacommons

import scala.collection.concurrent.TrieMap
import java.util.concurrent.locks.ReentrantReadWriteLock

//http://michid.wordpress.com/2009/02/23/function_mem/

//class Memoize1[-T, +R](_f: T => R) extends AbstractMemoize1(_f,_=>true)

//abstract class AbstractMemoize1[-T, +R](f: T => R, acceptResultForCaching: R => Boolean) extends (T => R) {

/**
 *
 * @param f
 * @tparam T  The input to the function
 * @tparam R  The generic output of the function (supporting covariance) 
 * @tparam Q  The actual output of the function (supporting acceptResultForCaching)
 */
class Memoize1[-T, +R, Q <: R](f: T => Q) extends (T => R) {

  val rwLock = new ReentrantReadWriteLock(true)
  val rLock = rwLock.readLock()
  val wLock = rwLock.writeLock()

  protected[this] val vals = TrieMap.empty[T, R] // mutable.Map.empty[T, R] with concurrent.map[T,R]

  def acceptResultForCaching(y: Q) = true


  // this doesn't work because a read lock can't be upgraded to a write lock.
  /*
  def apply(x: T): R = {
    // argh: TrieMap claims to be threadsafe, yet getOrElseUpdate is totally vulnerable to race conditions.
    // vals.getOrElseUpdate(x,f(x))

    rLock.lock()
    try {
      vals.get(x).getOrElse({
        // do the expensive part outside of the write lock.

        // todo: keep track of which computations are underway.
        // else there is a small risk of computing f(x) redundantly.
        // That could just waste time, but could also cause trouble if it has side effects.

        val y = f(x)

        if (acceptResultForCaching(y)) {
          wLock.lock()
          try {
            // if we computed f(x) redundantly, just retain whichever result won the race
            if (!vals.contains(x)) {
              vals += ((x, y))
            }
          }
          finally {
            wLock.unlock()
          }

          // if we computed f(x) redundantly, return the result that is actually in the cache
          vals(x)
        }
        else y
      })

    } finally {
      rLock.unlock()
    }

  }
  */

  // simple version
  /*
  def apply(x: T): R = synchronized {
    if (vals.contains(x)) {
      vals(x)
    }
    else {
      val y = f(x)
      vals += ((x, y))
      y
    }
  }
  */

  def apply(x: T): R = synchronized {
    rLock.lock()
    val c = try {
      vals.get(x)
    } finally {
      rLock.unlock()
    }

    val result = c.getOrElse({

      // expensive part is outside the write lock
      val y = f(x)

      if (acceptResultForCaching(y)) {
        wLock.lock()
        try {
          // check the cache again to make sure nobody else wrote it in the meantime
          vals.get(x).getOrElse({
            vals += ((x, y))
            y
          })
        } finally {
          wLock.unlock()
        }
      }
      else y
    })
    result
  }


  // don't call this "contains" since that could mislead re the contents of an underlying collection
  def isCached(x: T) = vals.contains(x)

  def getCached(x: T): Option[R] = vals.get(x)

}


//class OptionMemoize1[-T, +R](_f: T => R) extends AbstractMemoize1(_f,_=>true)

trait DropNone[-T, +R, Q <: R] extends Memoize1[T, Option[R], Option[Q]] {
  override def acceptResultForCaching(y: Option[Q]) = y.isDefined

  // since we're not caching None, we know that a None response from the cache means the underlying store was just checked.
  def getFlat(x: T): Option[R] = getCached(x).getOrElse(None) //  flatten
}

object Memoize1 {
  def apply[T, R](f: T => R) = new Memoize1(f)

  def Y[T, R](f: (T, T => R) => R) = {
    var yf: T => R = null
    yf = Memoize1(f(_, yf(_)))
    yf
  }
}

class InvalidatableMemoize1[-T, +R, Q <: R](f: T => Q) extends Memoize1[T, R, Q](f) {
  def remove(x: T) = vals.remove(x)

  def clear() {
    vals.clear()
  }
}

object InvalidatableMemoize1 {
  def apply[T, R](f: T => R) = new InvalidatableMemoize1(f)
}

trait Forceable1[-T, R, Q <: R] extends InvalidatableMemoize1[T, R, Q] {
  def force(x: T, y: R): Unit = vals.update(x, y)
}

object ForceableMemoize1 {
  def apply[T, R](f: T => R) = new InvalidatableMemoize1(f) with Forceable1[T, R, R]
}


class InvalidatableForceableOptionMemoize1[T, R](f: T => Option[R]) extends InvalidatableMemoize1[T, Option[R], Option[R]](f) with DropNone[T, R, R] with Forceable1[T, Option[R], Option[R]] {
  def forceFlat(x: T, y: R): Unit = force(x, Some(y))
}

//class test extends InvalidatableMemoize1[UUID, Option[T], Option[T]] with DropNone[UUID, T, T] with Forceable1[UUID, Option[T], Option[T]]

/*
class ConditionalMemoize1[-T, +R](f: T => R, condition: R => Boolean) extends (T => R)
	{

	import scala.collection.mutable

	protected[this] val vals = mutable.Map.empty[T, R]

	def apply(x: T): Option[R] =
		{
		if (vals.contains(x))
			{
			Some(vals(x))
			}
		else
			{
			val y = f(x)
			if (condition(y))
				{
				vals += ((x, y))
				Some(y)
				}
			else None
			}
		}
	}

object ConditionalMemoize1
	{
	def apply[T, R](f: T => R, condition: R => Boolean) = new ConditionalMemoize1(f, condition)

	def Y[T, R](f: (T, T => R) => R) =
		{
		var yf: T => R = null
		yf = Memoize1(f(_, yf(_)))
		yf
		}
	}
*/
/*
case class Memoize1[-T, +R](f: T => R) extends Function1[T, R]
  {

  import scala.collection.mutable

  private[this] val vals = mutable.Map.empty[T, R]

  def apply(x: T): R = vals.getOrElseUpdate(x, f(x))
  }

object RecursiveMemoizedFunction
  {
  def apply[T, R](fRec: (T, T => R) => R): (T => R) =
	{
	def f(n: T): R = fRec(n, n => f(n))
	Memoize1(f)
	}
  }
*/


// Memoize0 stuff can be accomplished with just a var or lazy val; be super explicit here for the sake of consistent API

class Memoize0[+R](f: => R) extends (() => R) {

  // this solution doesn't allow clearing
  lazy val cache = f

  protected[this] var it: Option[R] = None

  def apply(): R = synchronized {
    it.getOrElse({
      val y = f
      it = Some(y)
      y
    })
  }

  // don't call this "contains" since that could mislead re the contents of an underlying collection
  def isCached() = it.isDefined

  def getCached(): Option[R] = it
}

object Memoize0 {
  def apply[R](f: () => R) = new Memoize0(f)

}

class InvalidatableMemoize0[+R](f: => R) extends Memoize0[R](f) {

  def clear() {
    synchronized {
      it = None
    }
  }
}

object InvalidatableMemoize0 {
  def apply[R](f: () => R) = new InvalidatableMemoize0(f)
}

trait Forceable0[R] extends InvalidatableMemoize0[R] {
  def force(y: R) = synchronized {
    it = Some(y)
    y
  }
}

object ForceableMemoize0 {
  def apply[R](f: => R) = new InvalidatableMemoize0[R](f) with Forceable0[R] {}
}

class Memoize2[-S, -T, +R, Q <: R](f: (S,T) => Q) extends Memoize1[(S,T),R,Q]((x:(S,T))=>f(x._1,x._2)) {
  def apply(q:S,x: T): R = super.apply((q,x))
}

object Memoize2 {
  def apply[S, T, R](f: (S,T) => R) = new Memoize2(f)
}

class InvalidatableMemoize2[-S, -T, +R, Q <: R](f: (S,T) => Q) extends Memoize2[S, T, R, Q](f) {
  def remove(q:S,x: T) = vals.remove((q,x))

  def clear() {
    vals.clear()
  }
}

object InvalidatableMemoize2 {
  def apply[S, T, R](f: (S,T) => R) = new InvalidatableMemoize2(f)
}

trait Forceable2[-S, -T, R, Q <: R] extends InvalidatableMemoize2[S, T, R, Q] {
  def force(q:S, x: T, y: R): Unit = vals.update((q,x), y)
}

object ForceableMemoize2 {
  def apply[S, T, R](f: (S,T) => R) = new InvalidatableMemoize2(f) with Forceable2[S, T, R, R]
}


class InvalidatableForceableOptionMemoize2[S, T, R](f: (S,T) => Option[R]) extends InvalidatableMemoize2[S, T, Option[R], Option[R]](f) with DropNone[(S,T), R, R] with Forceable2[S, T, Option[R], Option[R]] {
  def forceFlat(q:S, x: T, y: R): Unit = force(q, x, Some(y))
}
