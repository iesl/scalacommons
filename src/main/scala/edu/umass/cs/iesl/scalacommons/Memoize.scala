package edu.umass.cs.iesl.scalacommons

import java.util.concurrent.locks.ReentrantReadWriteLock
import scala.collection.parallel.mutable.ParTrieMap

//http://michid.wordpress.com/2009/02/23/function_mem/

/**
 * the minimal functions needed to support a cache; a subset of mutable.MapLike.  The point is that we can make an
 * EhCache implementation without having to fully implement mutable.Map, because that would be overkill
 * (see https://github.com/vinaynair/EhCacheAsScalaMap)
 * @tparam T
 * @tparam R
 */
trait BasicMutableMap[T,R] {
  def update(key: T, value: R): Unit

  def clear() : Unit
  
  // an optimization for our weird shared Ehcache situation, so we can clear multiple subcaches without retrieving the keys each time
  // def clear(allKeys: GenIterable[_] ) : Unit = clear()

  def remove(value: T)

  def get(value: T): Option[R]

  //def contains(value: T):Boolean
}

trait Memoize1[-T, +R, Q <: R] extends (T => R) {

  val f: T=>Q

  protected[this] def vals: BasicMutableMap[T,R]

  // don't call this "contains" since that could mislead re the contents of an underlying collection
  //def isCached(x: T) = vals.contains(x)

  def getCached(x: T): Option[R] = vals.get(x)

  def acceptResultForCaching(y: Q) = true

  def apply(x: T): R
}

trait ParTrieMapMemoize1 [T, R, Q <: R] extends Memoize1[T,R,Q]{
  //new ParTrieMap[T,R]() with BasicMutableMap[T,R] //TrieMap.empty[T, R]  // mutable.Map.empty[T, R] with concurrent.map[T,R]
  private val m = ParTrieMap.empty[T,R]
  
  protected[this] val vals = new BasicMutableMap[T,R] {
    def clear() = m.clear
    //def contains(value: T) = m.contains(value)
    def get(value: T) = m.get(value)
    def remove(value: T) = m.remove(value)
    def update(key: T, value: R) = m.update(key,value)
  }
}

/**
 *
 * @tparam T  The input to the function
 * @tparam R  The generic output of the function (supporting covariance) 
 * @tparam Q  The actual output of the function (supporting acceptResultForCaching)
 */
trait RWLockingMemoize1[-T, +R, Q <: R] extends (T => R) with Memoize1[T,R,Q] {

  val rwLock = new ReentrantReadWriteLock(true)
  val rLock = rwLock.readLock()
  val wLock = rwLock.writeLock()

 
  override def apply(x: T): R =  {
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
            vals.update(x, y)
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


}


//class OptionMemoize1[-T, +R](_f: T => R) extends AbstractMemoize1(_f,_=>true)

trait DropNone[-T, +R, Q <: R] extends Memoize1[T, Option[R], Option[Q]] {
  override def acceptResultForCaching(y: Option[Q]) = y.isDefined

  // since we're not caching None, we know that a None response from the cache means the underlying store was just checked.
  def getFlat(x: T): Option[R] = getCached(x).getOrElse(None) //  flatten
}

object Memoize1 {
  def apply[T, R](_f: T => R) = new RWLockingMemoize1[T,R,R] with ParTrieMapMemoize1[T,R,R] {
    val f = _f
  }

  def Y[T, R](f: (T, T => R) => R) = {
    var yf: T => R = null
    yf = Memoize1(f(_, yf(_)))
    yf
  }
}


trait InvalidatableMemoize1[-T, +R, Q <: R] extends Memoize1[T, R, Q] {
  def remove(x: T) = vals.remove(x)
  
  def clear() {
    vals.clear()
  }
  
}

object InvalidatableMemoize1 {
  def apply[T, R](_f: T => R) = new RWLockingMemoize1[T,R,R]  with ParTrieMapMemoize1[T,R,R] with InvalidatableMemoize1[T,R,R] {
    val f = _f
  } 
}

trait Forceable1[-T, R, Q <: R] extends InvalidatableMemoize1[T, R, Q] {
  def force(x: T, y: R): Unit = vals.update(x, y)
}

object ForceableMemoize1 {
  def apply[T, R](_f: T => R) = new RWLockingMemoize1[T,R,R]  with ParTrieMapMemoize1[T,R,R] with InvalidatableMemoize1[T,R,R]  with Forceable1[T, R, R]{
    val f = _f
  }
}


trait InvalidatableForceableOptionMemoize1[T, R] extends InvalidatableMemoize1[T, Option[R], Option[R]] with DropNone[T, R, R] with Forceable1[T, Option[R], Option[R]] {
  def forceFlat(x: T, y: R): Unit = force(x, Some(y))
}


object InvalidatableForceableOptionMemoize1 {
  def apply[T, R](_f: T => Option[R]) = new RWLockingMemoize1[T,Option[R],Option[R]]  with ParTrieMapMemoize1[T,Option[R],Option[R]] with InvalidatableForceableOptionMemoize1[T,R] {
    val f = _f
  }
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


// Memoize0 stuff can be accomplished with just a var or lazy val; we're just super explicit here for the sake of consistent API

trait Memoize0[+R] extends (() => R) {
  val f: Unit=>R
  
  // this solution doesn't allow clearing
  lazy val cache = f

  protected[this] var it: Option[R] = None

  def apply(): R = synchronized {
    it.getOrElse({
      val y = f()
      it = Some(y)
      y
    })
  }

  // don't call this "contains" since that could mislead re the contents of an underlying collection
  def isCached() = it.isDefined

  def getCached(): Option[R] = it
}

object Memoize0 {
  def apply[R](_f: Unit => R) = new Memoize0[R] { val f = _f }

}

trait InvalidatableMemoize0[+R] extends Memoize0[R] {

  def clear() {
    synchronized {
      it = None
    }
  }
}

object InvalidatableMemoize0 {
  def apply[R](_f: Unit => R) = new InvalidatableMemoize0[R] { val f = _f }
}

trait Forceable0[R] extends InvalidatableMemoize0[R] {
  def force(y: R) = synchronized {
    it = Some(y)
    y
  }
}

object ForceableMemoize0 {
  def apply[R](_f: Unit => R) = new InvalidatableMemoize0[R] with Forceable0[R]  { val f = _f }
}

trait Memoize2[-S, -T, +R, Q <: R] extends Memoize1[(S,T),R,Q] {
  
  val g: (S,T) => Q
  
  final val f = (x:(S,T))=>g(x._1,x._2)
  
  def apply(q:S,x: T): R = apply((q,x))
}

object Memoize2 {
  def apply[S, T, R](_g: (S,T) => R) = new RWLockingMemoize1[(S,T),R,R]  with ParTrieMapMemoize1[(S,T),R,R] with Memoize2[S,T,R,R] { val g = _g }
}

trait InvalidatableMemoize2[-S, -T, +R, Q <: R] extends Memoize2[S, T, R, Q] {
  def remove(q:S,x: T) = vals.remove((q,x))

  def clear() {
    vals.clear()
  }
}

object InvalidatableMemoize2 {
  def apply[S, T, R](_g: (S,T) => R) =  new RWLockingMemoize1[(S,T),R,R]  with ParTrieMapMemoize1[(S,T),R,R]with InvalidatableMemoize2[S,T,R,R] { val g = _g }
}

trait Forceable2[-S, -T, R, Q <: R] extends InvalidatableMemoize2[S, T, R, Q] {
  def force(q:S, x: T, y: R): Unit = vals.update((q,x), y)
}

object ForceableMemoize2 {
  def apply[S, T, R](_g: (S,T) => R) =  new RWLockingMemoize1[(S,T),R,R]  with ParTrieMapMemoize1[(S,T),R,R]with Forceable2[S,T,R,R] { val g = _g }
}


trait InvalidatableForceableOptionMemoize2[S, T, R] extends InvalidatableMemoize2[S, T, Option[R], Option[R]] with DropNone[(S,T), R, R] with Forceable2[S, T, Option[R], Option[R]] {
  def forceFlat(q:S, x: T, y: R): Unit = force(q, x, Some(y))
}

object InvalidatableForceableOptionMemoize2 {
  def apply[S, T, R](_g: (S,T) => Option[R]) =  new RWLockingMemoize1[(S,T),Option[R],Option[R]] with ParTrieMapMemoize1[(S,T),Option[R],Option[R]] with InvalidatableForceableOptionMemoize2[S, T, R] { val g = _g }
}
