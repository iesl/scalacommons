package edu.umass.cs.iesl.scalacommons.layout
package treevis

import org.specs2.mutable
import scalaz._, scalaz.{Scalaz => Z}, Z.{node => _, _}
import TreeVis._

object TreeVisSpec extends mutable.Specification  { 
  "tree vis" should {
    "move tree" in {
      val tree = (0.0, unitExtent, "a").node(
        (0.0, unitExtent, "b").leaf
      )
      val expect = (1.0, unitExtent, "a").node(
        (1.0, unitExtent, "b").leaf
      )
      val actual = movetree(tree, 1.0)
      actual ≟ expect must_== true
    }

    "test moveextent" in {
      val expect = Seq((1.0, 1.0), (1.0, 1.0))
      moveextent(Seq((0.0, 0.0), (0.0, 0.0)), 1.0) ≟ expect must_== true
    }


    "test merge" in {
      val expect = Seq((1.0, 2.0), (1.0, 2.0))
      val actual = merge(
        Seq((1.0, 1.0), (1.0, 1.0)),
        Seq((0.0, 2.0), (0.0, 2.0)))
      actual ≟ expect must_== true
    }

    "test mergelist" in {  
      val expect = Seq((1.0, 2.0), (1.0, 2.0))
      val actual = mergelist(Seq(
        Seq((1.0, 1.0), (1.0, 1.0)),
        Seq((0.3, 0.3), (0.3, 0.3)),
        Seq((0.3, 0.3), (0.3, 0.3)),
        Seq((0.0, 2.0), (0.0, 2.0))))
      actual ≟ expect must_== true
    }


    "test fit" in {
      val actual = fit(
        Seq((1.0, 2.0), (1.0, 3.0)),
        Seq((1.0, 3.0), (1.0, 4.0)))
      val expect = 3
      actual ≟ expect must_== true
    }

    "test fitlistl" in {
      val es = Seq(
        Seq((1.0, 2.0), (1.0, 3.0)),
        Seq((1.0, 3.0), (1.0, 4.0))
      )

      val actual = fitlistl(es)
      val expect = Seq(0.0, 3.0)
      actual ≟ expect must_== true
    }
  }
}


