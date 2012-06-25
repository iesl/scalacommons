package edu.umass.cs.iesl.scalacommons

import com.weiglewilczek.slf4s.Logging

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */
object OptionUtils extends Logging
	{
	def mergeWarn[T](a: Option[T], b: Option[T]): Option[T] =
		{
		(a, b) match
		{
			case (Some(x), Some(y)) =>
				{
				if (x != y)
					{
					logger.warn("Merging unequal values, preferring: " + x + "  to " + y)
					}
				a
				}
			case (Some(x), None) => a
			case (None, Some(y)) => b
			case (None, None) => None
		}
		}

	def mergeOrFail[T](a: Option[T], b: Option[T]): Option[T] =
		{
		(a, b) match
		{
			case (Some(x), Some(y)) =>
				{
				if (x != y)
					{
					throw new OptionMergeException(x, y)
					}
				a
				}
			case (Some(x), None) => a
			case (None, Some(y)) => b
			case (None, None) => None
		}
		}
	}

class OptionMergeException[T](x: T, y: T) extends Exception("unequal values: " + x + "  ,  " + y)
