package edu.umass.cs.iesl.scalacommons




// obsoleted by Memoize1 with DropNone

//http://michid.wordpress.com/2009/02/23/function_mem/
/*
/**
 * Memoize a function that return Options, but do not memoize if the function return None
 * @param f
 * @tparam T
 * @tparam R
 */



class OptionMemoize1[-T, +R](f: T => Option[R]) extends (T => Option[R]) {

  import scala.collection.mutable

  // todo use scala 2.10 threadsafe map
  protected[this] val vals = mutable.Map.empty[T, R]

  def apply(x: T): Option[R] = synchronized {
    if (vals.contains(x)) {
      Some(vals(x))
    }
    else {
      val y = f(x)
      y.map(yy => vals += ((x, yy)))
      y
    }
  }

  // don't call this "contains" since that could mislead re the contents of an underlying collection
  def isCached(x: T) = vals.contains(x)

  def getCached(x: T): Option[R] = synchronized {
    vals.get(x)
  }
}


class OptionMemoize1[-T, +R](f: T => Option[R]) extends (T => Option[R]) {

  protected[this] val vals = TrieMap.empty[T, R] // mutable.Map.empty[T, R] with concurrent.map[T,R]

  def apply(x: T): R = vals.getOrElseUpdate(x,f(x))

  // don't call this "contains" since that could mislead re the contents of an underlying collection
  def isCached(x: T) = vals.contains(x)

  def getCached(x: T): Option[R] = vals.get(x)

}



object OptionMemoize1 {
  def apply[T, R](f: T => Option[R]) = new OptionMemoize1(f)

  def Y[T, R](f: (T, T => R) => R) = {
    var yf: T => R = null
    yf = Memoize1(f(_, yf(_)))
    yf
  }
}

class InvalidatableOptionMemoize1[-T, +R](f: T => Option[R]) extends OptionMemoize1[T, R](f) {
  def remove(x: T) = synchronized {
    vals.remove(x)
  }

  def clear() {
    synchronized {
      vals.clear()
    }
  }
}

object InvalidatableOptionMemoize1 {
  def apply[T, R](f: T =>  Option[R]) = new InvalidatableOptionMemoize1(f)
}

/**
 * Normally the only way to memoize the result of a function is to compute it.  Here, we also allow just asserting that y=f(x).
 */
trait OptionForceable[-T, R] extends InvalidatableOptionMemoize1[T, R] {
  def force(x: T, y: R) = synchronized {
    vals += ((x, y))
    y
  }
}

/**
 * Normally the only way to memoize the result of a function is to compute it.  Here, we also allow just asserting that y=f(x).
 */
 
object ForceableOptionMemoize1 {
  def apply[T, R](f: T => Option[R]) = new InvalidatableOptionMemoize1(f) with OptionForceable[T, R]
}

*/

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
