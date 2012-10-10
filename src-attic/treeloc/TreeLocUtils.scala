package edu.umass.cs.iesl.scalacommons
package treeloc

import edu.umass.cs.iesl.scalacommons.util.{StringOps, FileOps}
import scalaz._, scalaz.{Scalaz => Z}, Z.{node => _, _}


object TreeLocUtils {
  def pathToRoot(treeloc: TreeLoc[_]): Stream[Int] = 
    treeloc.lefts.length #:: treeloc.parents.map(_._1.length)


  type ParentAxisNode[A] = (Stream[Tree[A]], A, Stream[Tree[A]]);
  type ParentAxis[A] = Stream[(Stream[Tree[A]], A, Stream[Tree[A]])]



  def combineTreeLocs[A : Monoid](tl1: TreeLoc[A], tl2: TreeLoc[A]): TreeLoc[A] = {
    
    def zipParentAxisNode[M : Monoid]: (ParentAxisNode[M], ParentAxisNode[M]) => ParentAxisNode[M] = {
      case ((lefts1, a1, rights1), (lefts2, a2, rights2)) 
      => {
        (lefts1 ++ lefts2, a1 ⊹ a2, rights1 ++ rights2) 
      }}

    def zipAllParentAxes[M : Monoid](axis1: ParentAxis[M], axis2: ParentAxis[M]): ParentAxis[M] = {
      val (pad1, pad2) = (Math.max(0, axis2.length - axis1.length),
                          Math.max(0, axis1.length - axis2.length));

      val (padded1, padded2) = (padParents(axis1, pad1), 
                                padParents(axis2, pad2));

      (padded1.ʐ ⊛ padded2.ʐ){ zipParentAxisNode } 
    }

    def padParents[M : Monoid](parents: ParentAxis[M], n: Int): ParentAxis[M] = 
      parents ++ (mzero[Stream[Tree[M]]], mzero[M], mzero[Stream[Tree[M]]]).replicate[Stream](n)

    // combine holes, lefts, rights
    val hole = tl1.getLabel ⊹ tl2.getLabel
    val lefts = tl1.lefts ++ tl2.lefts
    val rights = tl1.rights ++ tl2.rights

    val parents = zipAllParentAxes(tl1.parents, tl2.parents)

    val newTree = hole.node((tl1.tree.subForest ++ tl2.tree.subForest):_*)

    loc(newTree, 
        lefts, rights, parents)

  }
}


object TreeDrawing {
  import TreeLocUtils._

  def drawTree[A, B >: A](tree: Tree[A])(implicit sh: Show[B]): String = {
    implicit val showa: Show[A] = sh ∙ (a => a)
    drawTree_(tree).foldMap(_ + "\n")
  }

  def drawSubTrees[A : Show](s: Stream[Tree[A]]): Stream[String] = {
    s match {
      case Stream.Empty => Stream.Empty
      case Stream(t) => shift("└ ", "  ", drawTree_(t))
      case t #:: ts =>  shift("├ ", "│ ", drawTree_(t)) append drawSubTrees(ts)
    }}

  def shift(first: String, other: String, s: Stream[String]): Stream[String] =
    s.ʐ <*> ((first #:: other.repeat[Stream]).ʐ ∘ ((_: String) + (_: String)).curried)

  /** A 2D String representation of this Tree, separated into lines. */
  def drawTree_[A, B >: A](tree: Tree[A])(implicit sh: Show[B]): Stream[String] = {
    implicit val showa: Show[A] = sh ∙ (x => x)
    tree.rootLabel.shows #:: drawSubTrees(tree.subForest)
  }
}


object TreeLocDrawing {
  import TreeLocUtils._
  import TreeDrawing._


  def drawTreeLoc[A, B >: A](treeloc: TreeLoc[A])(implicit sh: Show[B]): Stream[String] = {
    implicit val showa: Show[A] = sh contramap (x => x)
    
    val pathfrom:(Stream[Stream[String]], Stream[String], Stream[Stream[String]]) =
      (treeloc.lefts.reverse ∘ (drawTree_(_)),
       drawTree_(treeloc.tree),
       treeloc.rights ∘ (drawTree_(_)))


    val path: (Stream[Stream[String]], Stream[String], Stream[Stream[String]]) =
      treeloc.parents.reverse.foldr(pathfrom) {
        case ((plefts   : Stream[Tree[A]], 
               ppath    , 
               prights  : Stream[Tree[A]]), 
              (acclefts  : Stream[Stream[_]], 
               accmid    : Stream[_], 
               accrights : Stream[Stream[_]])) => {
                
                // acc is a stream of formatted trees, where each tree is a Stream[String]
                val lefts = acclefts ∘ (ss => shift("┠─", "┃ ", ss))

                val mid = 
                  if   (!accrights.isEmpty) shift("┡━", "│ ", accmid)
                  else                      shift("┗━", "  ", accmid) 

                val rights = accrights.splitAt(accrights.length-1) 
                val r1 = rights._1 ∘ (ss => shift("├─", "│ ", ss))
                val r2 = rights._2 ∘ (ss => shift("└─", "  ", ss))

                val middle = Stream(ppath.shows) ++ lefts.join ++ mid ++ (r1 ++ r2).join
                val pathfrom:(Stream[Stream[String]], Stream[String], Stream[Stream[String]]) =
                  (plefts.reverse ∘ (drawTree_(_)), 
                   middle, 
                   prights ∘ (drawTree_(_)))
                 
                pathfrom
              }}

    path match {
      case (l, m, r) => l.join ++ m ++ r.join
    }
  }


  def renderTreeLoc[A, B >: A](treeloc: TreeLoc[A])(implicit sh: Show[B]): String = {
    implicit val showa: Show[A] = sh contramap (x => x)
    val lines = drawTreeLoc(treeloc)
    (lines ∘ (_.mkString("\n"))).mkString("\n")
  }
}

