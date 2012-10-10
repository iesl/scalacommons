package edu.umass.cs.iesl.scalacommons.layout
package treevis

// import collection.mutable.{ HashSet, MultiMap, HashMap, ArrayBuffer }
// import org.bson.types.ObjectId
// import cc.rexa2.data.MongoDataLayer
// import com.mongodb.casbah.Imports._
// import MongoConfig.{authorEntityColl, paperEntityColl, venueEntityColl}
// import scala.{ xml => sxml }
// import scala.xml.{ Node, NodeSeq, NodeBuffer, Elem, Group }
// import scalaj.collection.Imports._
// import net.liftweb.util._
// import Helpers._
// import ScalazUtils._
// import expansions._
// import utils._
// import net.liftweb._
// import net.liftweb.common._

object TreeVis {
  import scalaz._
  import Scalaz._

  //  Tree formatting library, based on ...
  //  Tree extents are represented as a list of pairs of reals [[r1, r2], ...]
  //  The width of the root of the tree is contained in the head of the list,
  //  level 2 at pos 2, etc.

  // type PosLabel[A] = ({type λ[α]=(Double, Extent, α)})#λ[A]


  /// type PosLabelX[A]
  type PosLabel[A]=(Double, Extent, A)

  type Span = (Double, Double);
  type Extent = Seq[Span];

  def unitSpan = (0.0, 0.0)
  def unitExtent = Seq[Span]()

  // case class Layout(x:Double, extent:Extent)

  def movetree[A](tree: Tree[PosLabel[A]], x: Double): Tree[PosLabel[A]] = {
    tree.map {case (pos, ext, label) => (pos+x, ext, label)}
  }


  def merge: (Extent, Extent) => Extent = 
    (e1s, e2s) => {
      (e1s, e2s).zipped.map {
        case (e1, e2) => (e1._1, e2._2)
      } ++ {
        if (e1s.length < e2s.length)
          e2s.view(e1s.length, e2s.length)
        else
          e1s.view(e2s.length, e1s.length)
      }
    }

  // Translate an extent by x
  def moveextent: (Extent, Double) => Extent = 
    (ex, x) => ex map (e => (e._1+x, e._2+x))

  // Generalize merge to a list of extents
  def mergelist: (Seq[Extent]) => Extent =
    exts => 
      exts.foldLeft(Seq[(Double, Double)]())(merge)




  // Find the minimum distance needed to fit two subtree extents side-by-side,
  // assuming a distance of 1.0 between them when rendered out.
  def fit: (Extent, Extent) => Double = 
    (e1s, e2s) => 
      (e1s, e2s).zipped.foldLeft(0.0) {
        case (a, ((_, p), (q, _))) => 
          math.max(a, p - q + 1.0)
      }



  // fitlistl = (es) ->
  //   fitlistlp = (acc, [e, es...]) ->
  //     if e?
  //       x = fit(acc, e)
  //       [x, fitlistlp(merge(acc, moveextent(e,x)), es)...]
  //     else []
  //   fitlistlp([], es)

  def fitlistl: (Seq[Extent]) => Seq[Double] =
    extents =>  {
      val accExt = Seq[Span]()
      val accFit = Seq[Double]()
      val res = extents.foldLeft(
        (accExt, accFit)) {
          case (acc@(exts, fits), e) => {
            val x = fit(exts, e)
            val m = merge(exts, moveextent(e, x))
            (m, fits ++ Seq(x))
          }
        }
      res._2
    }
 

  // fun mean (x,y) = (x+y)/2.0
  // fun fitlist es = map mean (zip (fitlistl es, fitlistr es))
  def fitlist: (Seq[Extent]) => Seq[Double] = fitlist

//   design = (tree) ->
//     designp = ([label, subtrees...]) ->
//       ## console.log("label/sub = ", label, subtrees)
//       if subtrees.length > 0
//         [trees, extents] = fp.unzip(designp(t) for t in subtrees)
//         # trees = ([t] for t in trees)
//         ##   # console.log("trees..", formatlist(trees))
//         ##   # console.log("extents..", formatlist(extents))
//         positions = fitlist(extents)
//         ##   # console.log("positions..", positions)
//         ##   # console.log("fp.zip(trees, positions).. ", formatlist(fp.zip(trees, positions)))
//         ptrees   = (movetree(x...) for x in fp.zip(trees, positions))
//         ##   # console.log("ptrees..", formatlist(ptrees))
//         ##   # console.log("fp.zip(extents, positions)...", formatlist(fp.zip(extents, positions)))
//         pextents = (moveextent(x...) for x in fp.zip(extents, positions))
//         ##   # console.log("pextents..", formatlist(pextents))
//         resultextent = [[0.0, 0.0],  mergelist(pextents)...]
//         ##   # console.log("resultextent..", formatlist(resultextent))
//         resulttree = [[label, 0.0], ptrees...]
//         [resulttree, resultextent]
//       else
//         [[[label, 0.0]], [[0.0, 0.0]]]
//     (designp(tree))[0]


  // def scanr[B](g: (A, Stream[Tree[B]]) => B): Tree[B]

  def design[A](tree: Tree[A]): Tree[PosLabel[A]] = {
    def f: (A, Stream[Tree[PosLabel[A]]]) => PosLabel[A] = {
      case (a, Stream.Empty) =>
        (0.0, Seq[Span](), a)

      case (a, subtrees) =>
        (0.0, Seq[Span](), a)
        
    }

    tree.scanr(f)
  }


//       case (label:A, Stream.Empty) => (0.0, label).leaf
//       case (label:A, subtrees:Stream[Tree[PosLabel[A]]]) => {
//         // val (trees, extents) = fp.unzip(designp(t) for t in subtrees)
//         // val positions = fitlist(extents)
//         // val ptrees   = (movetree(x...) for x in fp.zip(trees, positions))
//         // val pextents = (moveextent(x...) for x in fp.zip(extents, positions))
//         // val resultextent = [[0.0, 0.0],  mergelist(pextents)...]
//         // val resulttree = [[label, 0.0], ptrees...]
//         // (resulttree, resultextent)
//         (0.0, Seq(), label).leaf
//       }})

//   def design[A](tree: Tree[A]): Tree[PosLabel[A]] = {
//     def designp(tree: Tree[A]):  Tree[PosLabel[A]] = {
//        if (subtrees.length > 0) {
//          val (trees, extents) = fp.unzip(designp(t) for t in subtrees)
//          val positions = fitlist(extents)
//          val ptrees   = (movetree(x...) for x in fp.zip(trees, positions))
//          val pextents = (moveextent(x...) for x in fp.zip(extents, positions))
//          val resultextent = [[0.0, 0.0],  mergelist(pextents)...]
//          val resulttree = [[label, 0.0], ptrees...]
//          (resulttree, resultextent)
//        }
//        else
//          [[[label, 0.0]], [[0.0, 0.0]]]
//      (designp(tree))[0]
//   }

}


