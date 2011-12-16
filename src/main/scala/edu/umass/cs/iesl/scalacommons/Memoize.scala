package edu.umass.cs.iesl.scalacommons


//http://michid.wordpress.com/2009/02/23/function_mem/
class Memoize1[-T, +R](f: T => R) extends (T => R) {

  import scala.collection.mutable

  private[this] val vals = mutable.Map.empty[T, R]

  def apply(x: T): R = {
    if (vals.contains(x)) {
      vals(x)
    }
    else {
      val y = f(x)
      vals += ((x, y))
      y
    }
  }
}

object Memoize1 {
  def apply[T, R](f: T => R) = new Memoize1(f)

  def Y[T, R](f: (T, T => R) => R) = {
    var yf: T => R = null
    yf = Memoize1(f(_, yf(_)))
    yf
  }
}

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
