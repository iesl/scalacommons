package edu.umass.cs.iesl.scalacommons.collections

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfter, Spec}

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */
class PrioritySetTest extends Spec with ShouldMatchers with BeforeAndAfter
	{

	describe("A priority set of integers")
	{
	val ord = new Ordering[Int]
		{
		def compare(x: Int, y: Int) = x.compare(y)
		}

	val ps = new PrioritySet[Int](ord)
	ps += 6 += 8 += 8 += 3 += 4 += 9 += 2 += 3 += 9 += 1 += 5 += 7

	it("drops duplicates")
	{
	ps.toSet should be === Set(1, 2, 3, 4, 5, 6, 7, 8, 9)
	}
	it("returns the largest element first")
	{
	ps.dequeue() should be === 9
	}
	it("returns remaining elements in the correct order")
	{
	ps.dequeueAll should be === IndexedSeq(8, 7, 6, 5, 4, 3, 2, 1)
	}
	}


	describe("A reverse priority set of integers")
	{
	val ord = new Ordering[Int]
		{
		def compare(x: Int, y: Int) = -x.compare(y)
		}

	val ps = new PrioritySet[Int](ord)
	ps += 6 += 8 += 8 += 3 += 4 += 9 += 2 += 3 += 9 += 1 += 5 += 7

	it("drops duplicates")
	{
	ps.toSet should be === Set(1, 2, 3, 4, 5, 6, 7, 8, 9)
	}
	it("returns the smallest element first")
	{
	ps.dequeue() should be === 1
	}
	it("returns remaining elements in the correct order")
	{
	ps.dequeueAll should be === IndexedSeq(2, 3, 4, 5, 6, 7, 8, 9)
	}
	}
	}
