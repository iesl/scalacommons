package edu.umass.cs.iesl.scalacommons

import org.scalatest.FunSuite
import SeqUtils._

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 */
class SeqUtilsTest extends FunSuite {


  test("argmax works with a Seq as an input") {
    val input = Seq[Double](-2, -1, 0, 1, 2)
    def f(a: Double): Double = a * a
    val result = argMax(input, f)
    assert(result === Set(-2, 2))
  }

  test("argmax works with a Set as an input") {
    val input = Set[Double](-2, -1, 0, 1, 2)
    def f(a: Double): Double = a * a
    val result = argMax(input, f)
    assert(result === Set(-2, 2))
  }

  test("argmin works with a Set as an input") {
    val input = Set[Double](-2, -1, 0, 1, 2)
    def f(a: Double): Double = a * a
    val result = argMin(input, f)
    assert(result === Set(0))
  }
}
