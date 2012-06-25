package edu.umass.cs.iesl.scalacommons

import com.weiglewilczek.slf4s.Logging

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */
object SeqUtils extends Logging
	{

	implicit def emptyCollectionToNone[T <: Iterable[Any]](s: T): Option[T] = if (s.isEmpty) None else Some(s)

	def mergeWarn[T](a: Seq[T], b: Seq[T]): Seq[T] =
		{
		(a, b) match
		{
			case (Nil, Nil) => Nil
			case (p, Nil) => a
			case (Nil, q) => b
			case (p, q) =>
				{
				if (p != q)
					{
					logger.warn("Merging unequal sequences, preferring: " + p + "  to " + q)
					}
				a
				}
		}
		}

	def mergeOrFail[T](a: Seq[T], b: Seq[T]): Seq[T] =
		{
		(a, b) match
		{
			case (Nil, Nil) => Nil
			case (p, Nil) => a
			case (Nil, q) => b
			case (p, q) =>
				{
				if (p != q)
					{
					throw new SeqMergeException(p, q)
					}
				a
				}
		}
		}
	}

class SeqMergeException[T](x: T, y: T) extends Exception("unequal sequences: " + x + "  ,  " + y)
