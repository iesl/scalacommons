package edu.umass.cs.iesl.scalacommons

import scala.collection.concurrent.TrieMap

//http://michid.wordpress.com/2009/02/23/function_mem/
class Memoize1[-T, +R](f: T => R) extends (T => R) {

  protected[this] val vals = TrieMap.empty[T, R] // mutable.Map.empty[T, R] with concurrent.map[T,R]

  def apply(x: T): R = vals.getOrElseUpdate(x,f(x))

  // don't call this "contains" since that could mislead re the contents of an underlying collection
  def isCached(x: T) = vals.contains(x)

  def getCached(x: T): Option[R] = vals.get(x)
  
}

object Memoize1 {
  def apply[T, R](f: T => R) = new Memoize1(f)

  def Y[T, R](f: (T, T => R) => R) = {
    var yf: T => R = null
    yf = Memoize1(f(_, yf(_)))
    yf
  }
}

class InvalidatableMemoize1[-T, +R](f: T => R) extends Memoize1[T, R](f) {
  def remove(x: T) = vals.remove(x)
  def clear() { vals.clear() }
}

object InvalidatableMemoize1 {
  def apply[T, R](f: T => R) = new InvalidatableMemoize1(f)
}

trait Forceable1[-T, R] extends InvalidatableMemoize1[T, R] {
  def force(x: T, y: R) = vals.update(x,y)
}

object ForceableMemoize1 {
  def apply[T, R](f: T => R) = new InvalidatableMemoize1(f) with Forceable1[T, R]
}



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


// Memoize0 stuff can be accomplished with just a var; be super explicit here for the sake of consistent API

class Memoize0[+R](f: () => R) extends (() => R) {


  protected[this] var it : Option[R] = None

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
  def apply[R](f: () => R) = new Memoize0(f)

}

class InvalidatableMemoize0[+R](f: () => R) extends Memoize0[R](f) {
  
  def clear() {
    synchronized {
     it = None
    }
  }
}

object InvalidatableMemoize0 {
  def apply[R](f: () => R) = new InvalidatableMemoize0(f)
}

trait Forceable0[ R] extends InvalidatableMemoize0[R] {
  def force( y: R) = synchronized {
    it = Some(y)
    y
  }
}

object ForceableMemoize0 {
  def apply[R](f: () => R) = new InvalidatableMemoize0(f) with Forceable0[R]
}

