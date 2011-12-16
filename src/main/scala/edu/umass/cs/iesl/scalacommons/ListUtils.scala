package edu.umass.cs.iesl.scalacommons

import collection.immutable

object ListUtils {

  def contiguousRuns[K, A](s: List[A])(f: A => K): immutable.List[(K, List[A])] = {
    if (s.isEmpty) Nil
    else {
      val p: List[(K, List[A])] = contiguousRuns(s.tail)(f)

      val a: A = s.head
      val k: K = f(a)

      if (p.isEmpty) {
        List((k, List(a)))
      }
      else {
        val (key, values) = p.head

        if (k == key) {
          (k, a :: values) :: p.tail
        }
        else {
          (k, List(a)) :: p
        }
      }
    }
  }

  def collapseShortRuns[K, A](runs: List[(K, List[A])], minLength: Int): immutable.List[(K, List[A])] = {
    if (runs.isEmpty) Nil
    else if (runs.length == 1) runs
    else {
      val (key, values) = runs.head
      val next = collapseShortRuns(runs.tail, minLength)

      val (nextkey, nextvalues) = next.head
      if (values.length < minLength || key == nextkey) {
        ((nextkey, values ::: nextvalues)) :: next.tail
      }
      else {
        runs.head :: next
      }
    }
  }

}
