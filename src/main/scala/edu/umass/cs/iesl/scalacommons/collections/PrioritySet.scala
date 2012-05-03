package edu.umass.cs.iesl.scalacommons.collections

import collection.mutable.{Set, HashSet, PriorityQueue}

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */
class PrioritySet[A](implicit val ord: Ordering[A]) extends PriorityQueue[A]
	{
	// some other data structure might be more efficient, but the redundant solution is quick and easy for now
	private val elementSet: Set[A] = HashSet[A]()

	override def +=(elem: A): this.type =
		{
		if (!elementSet.contains(elem))
			{elementSet += elem; super.+=(elem)}
		else this
		}

	override def dequeue(): A =
		{
		val a = super.dequeue();
		elementSet.remove(a);
		a
		}
	}

/**
 * Instead of an ordering, provide a function that computes a comparable priority value, and cache that
 */
trait ComputedPrioritySet[A] extends PrioritySet[A]
	{

	}
/**
 * A PrioritySet with the property that adding or removing an element may alter the priorities of some of the _other_ elements.
 */
abstract trait DynamicPrioritySet[A] extends PrioritySet[A]

/**
 * When some of the other elements are affected, immediately recompute their qualities and requeue them
 */
trait EagerDynamicPrioritySet[A] extends DynamicPrioritySet[A]
	{

	override def +=(elem: A): this.type =
		{

		val otherElems = elementSet.filter(isAffectedBy(_, elem))
		for (o <- otherElems)
			{
			remove(o)
			super.enqueue(o) // the implicit ordering will kick in and possibly return a different result this time
			}

		super.+=(elem)
		}
}

	/**
	 * If the only alterations allowed to priorities decrease them, then we can wait to recompute an element priority until we see it
	 */
	class LazyDecreasingDynamicPrioritySet[A] extends DynamicPrioritySet[A]
