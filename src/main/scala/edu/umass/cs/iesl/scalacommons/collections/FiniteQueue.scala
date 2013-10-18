package edu.umass.cs.iesl.scalacommons.collections

// http://stackoverflow.com/questions/6918731/maximum-length-for-scala-queue/6920366#6920366

import scala.collection.immutable.Queue
import scala.collection.mutable

class FiniteQueue[A](q: Queue[A]) {
  def enqueueFinite[B >: A](elem: B, maxSize: Int): Queue[B] = {
    var ret = q.enqueue(elem)
    while (ret.size > maxSize) {
      ret = ret.dequeue._2
    }
    ret
  }
  // slow
  /*
  def enqueueFinite[B >: A](elems: Iterator[B], maxSize: Int): Queue[B] = {
    elems.foldLeft(this)(x=>enqueueFinite(x,maxSize))
  }*/
}

object FiniteQueue {
  implicit def queue2finitequeue[A](q: Queue[A]) = new FiniteQueue[A](q)
}


class FiniteMutableQueue[A](q: mutable.Queue[A]) {
  def enqueueFinite[B <: A](elem: B, maxSize: Int) : Unit = {
    q.enqueue(elem)
    while (q.size > maxSize) {
      q.dequeue()
    }
  }
  def enqueueFinite[B <: A](elems: Iterator[B], maxSize: Int) : Unit= {
   elems.map(enqueueFinite(_,maxSize))
  }
}


object FiniteMutableQueue {
  implicit def queue2finitequeue[A](q: mutable.Queue[A]) = new FiniteMutableQueue[A](q)
}
