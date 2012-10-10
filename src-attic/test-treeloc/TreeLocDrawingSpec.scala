package edu.umass.cs.iesl.scalacommons
package treeloc

import org.specs2.mutable

import scalaz._, scalaz.{Scalaz => Z}, Z.{node => _, _}

import edu.umass.cs.iesl.scalacommons.util.{StringOps, FileOps}
import edu.umass.cs.iesl.scalacommons.layout.boxter.Boxes

import StringOps._
import FileOps._
import Boxes._


object TreeLocDrawingSpec extends mutable.Specification {
/*  import TreeLocDrawing._

  "treeloc drawing" should {
    "render all treelocs like so" in {
      val renderedTreeLocs = stripBorder("""
      |     0           0           0           0           0           0           0           0           0           0      |
      |     ├ 1         ┡━1         ┡━1         ┡━1         ┡━1         ┡━1         ┡━1         ┡━1         ┡━1         ┠─1    |
      |     │ ├ 2       │ ├ 2       │ ┡━2       │ ┡━2       │ ┡━2       │ ┠─2       │ ┠─2       │ ┠─2       │ ┠─2       ┃ ├ 2  |
      |     │ │ ├ 3     │ │ ├ 3     │ │ ├ 3     │ │ ┡━3     │ │ ┠─3     │ ┃ ├ 3     │ ┃ ├ 3     │ ┃ ├ 3     │ ┃ ├ 3     ┃ │ ├ 3|
      |     │ │ └ 6     │ │ └ 6     │ │ └ 6     │ │ └─6     │ │ ┗━6     │ ┃ └ 6     │ ┃ └ 6     │ ┃ └ 6     │ ┃ └ 6     ┃ │ └ 6|
      |     │ ├ 4       │ ├ 4       │ ├─4       │ ├─4       │ ├─4       │ ┡━4       │ ┠─4       │ ┠─4       │ ┠─4       ┃ ├ 4  |
      |     │ └ 8       │ └ 8       │ └─8       │ └─8       │ └─8       │ └─8       │ ┗━8       │ ┗━8       │ ┗━8       ┃ └ 8  |
      |     │   ├ 3     │   ├ 3     │   ├ 3     │   ├ 3     │   ├ 3     │   ├ 3     │   ├ 3     │   ┡━3     │   ┠─3     ┃   ├ 3|
      |     │   └ 4     │   └ 4     │   └ 4     │   └ 4     │   └ 4     │   └ 4     │   └ 4     │   └─4     │   ┗━4     ┃   └ 4|
      |     └ 5         └─5         └─5         └─5         └─5         └─5         └─5         └─5         └─5         ┗━5    |
      """)

      val tree = 0.node(1.node(2.node(3.leaf, 6.leaf), 4.leaf, 8.node(3.leaf, 4.leaf)), 5.leaf)
      val treeOfTreeLocs = tree.loc.cojoin.toTree
      val row = treeOfTreeLocs.flatten.foldl(text("")) { case (acc, tl) => {
        val tboxes = drawTreeLoc(tl) ∘ (text(_))
        val tbox = vcat(AlignFirst)(tboxes.toList)
        acc +| Boxes.text("   ") +| tbox
      }}

      val actual:List[String] = renderBox(row)

      val trimmed = (actual).mkString("\n")

      trimmed must_== renderedTreeLocs
    }
  }*/
}
