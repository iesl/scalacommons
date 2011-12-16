package edu.umass.cs.iesl.scalacommons

import collection.Seq
import collection.immutable.Map

object StatsUtils {
  def histogram[T](list: Seq[T]): Map[T, Int] = {

    val groups: Map[T, Seq[T]] = list groupBy (identity) // groupby does not keep multiple identical items!
    val counts = groups map {
      case (c, cs) => (c, cs.length)
    }

    counts
  }

  def histogramAccumulate[T](weightedList: Seq[(T, Int)]): Map[T, Int] = {
    val groups: Map[T, Seq[(T, Int)]] = weightedList groupBy (x => x._1) // does not work !?
    val counts = groups map {
      case (q, blocks) => (q, blocks.foldLeft(0)((acc, x) => acc + x._2))
    }

    counts
  }


  import scala.math._

  def squaredDifference(value1: Double, value2: Double) = pow(value1 - value2, 2.0)

  def stdDev(list: List[Double], average: Double) = {
    list.isEmpty match {
      case false =>
        val squared = list.foldLeft(0.0)(_ + squaredDifference(_, average))
        sqrt(squared / list.length.toDouble)
      case true => 0.0
    }
  }


}
