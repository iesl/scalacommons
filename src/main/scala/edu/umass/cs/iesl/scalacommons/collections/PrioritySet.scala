package edu.umass.cs.iesl.scalacommons.collections

import scala.annotation.tailrec
import edu.umass.cs.iesl.scalacommons.InvalidatableMemoize1
import scala._
import collection.mutable

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */
class PrioritySet[A](override val ord: Ordering[A]) extends mutable.PriorityQueue[A]()(ord) {
  // some other data structure might be more efficient, but the redundant solution is quick and easy for now
  protected val elementSet: mutable.Set[A] = mutable.HashSet[A]()

  override def +=(elem: A): this.type = {
    if (!elementSet.contains(elem)) {
      elementSet += elem; super.+=(elem)
    }
    else this
  }

  override def dequeue(): A = {
    val a = super.dequeue()
    elementSet.remove(a)
    a
  }
}

/*
trait ConditionalPrioritySet[A] extends PrioritySet[A]
	{
	def condition(a: A): Boolean

	override def +=(elem: A): this.type =
		{
		if (condition(elem))
			{super.+=(elem)}
		else this
		}
	}
*/
/**
 * Instead of an ordering, provide a function that computes a comparable priority value.  Priority values must remain stable,
 * so we cache them internally.
 */
class MemorizedPriorityOrdering[A, P <: Ordered[P]](priority: (A) => P) extends Ordering[A] {
  val mpriority = InvalidatableMemoize1[A, P](priority)

  def compare(x: A, y: A) = mpriority(x).compare(mpriority(y))
}

// this just serves to make the mpo available to subclasses
class MemorizedPrioritySet[A, P <: Ordered[P]](val mpo: MemorizedPriorityOrdering[A, P]) extends PrioritySet[A](mpo)

class ComputedPrioritySet[A, P <: Ordered[P]](priority: (A => P)) extends MemorizedPrioritySet[A, P](new MemorizedPriorityOrdering(priority)) {
  override def dequeue(): A = {
    val a: A = super.dequeue()
    mpo.mpriority.remove(a) // no need to remember priorities for dequeued items
    a
  }
}

/**
 * If the priority for a given item may become invalid, but the only alterations allowed to priorities decrease them,
 * then we can wait to recompute an element priority until we see it.
 * Caching is managed internally, so don't pass a memoized priority function!
 */
class LazyRecomputingPrioritySet[A, P <: Ordered[P]](priority: (A => P), priorityIsValid: (A => Boolean))
  extends MemorizedPrioritySet[A, P](new MemorizedPriorityOrdering(priority)) {
  @tailrec
  override final def dequeue(): A = {
    val a = super.dequeue()

    val p = mpo.mpriority.remove(a) // always forget a cached priority upon dequeuing, since it is either invalid or no longer needed anyway

    if (priorityIsValid(a)) {
      a
    }
    else {
      // re-enqueue this item; its priority was just forgotten so it will be automatically recomputed
      this += a

      // take the new best item
      dequeue()
    }
  }
}

/*
/**
 * A cache for priority values which keeps track of the "time" when a priority was computed, so as to tell whether a value is up to date.
 */
class GenerationalPriorityOrdering[A, P <: Ordered[P]](val priority: (A) => P) extends Ordering[A]
	{
	var currentGeneration: Int = 0

	def increment = currentGeneration += 1

	val validInGenerations = mutable.Map[A, Int]()

	// record the generation at which each priority is computed
	def vpriority(a: A): P =
		{
		validInGenerations.put(a, currentGeneration)
		priority(a)
		}

	val mpriority = InvalidatableMemoize1(vpriority)

	def remove(a: A) = mpriority.remove(a)

	def compare(x: A, y: A) = mpriority(x).compare(mpriority(y))

	def priorityIsCurrent(a: A) =
		{validInGenerations(a) == currentGeneration}
	}

object GenerationalRecomputingPrioritySet
	{
	def apply[A, P <: Ordered[P]](priority: (A => P)) =
		{
		val ord = new GenerationalPriorityOrdering[A, P](priority)
		new GenerationalRecomputingPrioritySet[A, P](ord)
		}
	}

object GenerationalRecomputingPrioritySetWithLowerBound
	{

	def apply[A, P <: Ordered[P]](priority: (A => Option[P])) =
		{
		val ord = new GenerationalPriorityOrdering[A, P](priority)
		new GenerationalRecomputingPrioritySet[A, P](ord)
		}
	}

/**
 * A LazyRecomputingPrioritySet where old priorities may become invalid only through the addition of new items to the set
 */
class GenerationalRecomputingPrioritySet[A, P <: Ordered[P]](ord: GenerationalPriorityOrdering[A, P])
		extends LazyRecomputingPrioritySet(ord.priority, ord.priorityIsCurrent)
	{
	override def +=(elem: A): this.type =
		{
		val result = super.+=(elem)
		if (result != this) ord.increment
		result
		}

	@tailrec
	override def dequeue(): A =
		{
		val a = super.dequeue()
		ord.remove(a) // always forget a cached priority upon dequeuing, since it is either invalid or no longer needed anyway

		if (ord.priorityIsCurrent(a))
			{
			a
			}
		else
			{
			// re-enqueue this item; its priority was just forgotten so it will be automatically recomputed
			this += a

			// take the new best item
			dequeue()
			}
		}
	}
*/
/**
 * A PrioritySet with the property that adding or removing an element may alter the priorities of some of the _other_ elements.
 */
//abstract trait DynamicPrioritySet[A] extends PrioritySet[A]
/**
 * When some of the other elements are affected, immediately recompute their qualities and requeue them
 *
 * actually we can't do this easily because PriorityQueue does not provide remove or -=.
 */
/*
class EagerDynamicPrioritySet[A](val isAffectedBy : ((A,A)=>Boolean)) extends DynamicPrioritySet[A]
	{

	override def +=(elem: A): this.type =
		{
		val otherElems = elementSet.filter(isAffectedBy(_, elem))
		for (o <- otherElems)
			{
			this -= (o)  // **
			super.enqueue(o) // the implicit ordering will kick in and possibly return a different result this time
			}

		super.+=(elem)
		}
	}
*/
