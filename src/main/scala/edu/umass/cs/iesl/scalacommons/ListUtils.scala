package edu.umass.cs.iesl.scalacommons

import scala.collection.immutable
import scala._

object ListUtils
	{

	// see also split below
	def contiguousRuns[K, A](s: List[A])(f: A => K): immutable.List[(K, List[A])] =
		{
		if (s.isEmpty) Nil
		else
			{
			val p: List[(K, List[A])] = contiguousRuns(s.tail)(f)

			val a: A = s.head
			val k: K = f(a)

			if (p.isEmpty)
				{
				List((k, List(a)))
				}
			else
				{
				val (key, values) = p.head

				if (k == key)
					{
					(k, a :: values) :: p.tail
					}
				else
					{
					(k, List(a)) :: p
					}
				}
			}
		}

	def collapseShortRuns[K, A](runs: List[(K, List[A])], minLength: Int): immutable.List[(K, List[A])] =
		{
		if (runs.isEmpty) Nil
		else if (runs.length == 1) runs
		else
			{
			val (key, values) = runs.head
			val next = collapseShortRuns(runs.tail, minLength)

			val (nextkey, nextvalues) = next.head
			if (values.length < minLength || key == nextkey)
				{
				((nextkey, values ::: nextvalues)) :: next.tail
				}
			else
				{
				runs.head :: next
				}
			}
		}

	//http://stackoverflow.com/questions/4761386/scala-list-function-for-grouping-consecutive-identical-elements, Landei answer
	def split[T](l: List[T])(p: T => Boolean): List[List[T]] =
		l.headOption.map
		{
		x => val (h, t) = l.span {p}; h :: split(t)(p)
		}.getOrElse(Nil)

	def groupContiguousSimilar[T](similar: (T, T) => Boolean)(l: List[T]): List[List[T]] =
		{

		// note that precontext accumulates things backwards: both the groups, and the sublists

		def merge(precontext: List[List[T]], r: T): List[List[T]] =
			{
			precontext match
			{
				case Nil => List(List(r))
				case h :: t =>
					{
					if (similar(h.head, r))
						// the immediately previous item is similar to this one
						// so prepend it to the current sublist
						{(r :: h) :: t}
					else // start a new sublist
						{List(r) :: precontext}
					}
			}
			}

		val rr : List[List[T]] = l.foldLeft[List[List[T]]](Nil)(merge)

		// reverse the outer and the inner lists
		rr.reverse.map(_.reverse)
		}
	}
