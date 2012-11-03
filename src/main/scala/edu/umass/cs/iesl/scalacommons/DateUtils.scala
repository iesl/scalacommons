package edu.umass.cs.iesl.scalacommons

import collection.mutable
import java.util.Locale
import java.text.DateFormatSymbols

object DateUtils {

  lazy val zeroBasedMonthsByName: Map[String, Int] = {
    val result: mutable.Map[String, Int] = mutable.Map[String, Int]()
    for (l <- Locale.getAvailableLocales) {
      val months = new DateFormatSymbols(l).getMonths
      for (i <- 0 until months.length) {
        result += months(i) -> i
      }

      val shortmonths = new DateFormatSymbols(l).getShortMonths
      for (i <- 0 until shortmonths.length) {
        result += shortmonths(i) -> i
      }

      for (i <- 1 to 12) {
        result += i.toString -> (i - 1)
      }
      for (i <- 1 to 9) {
        result += ("0" + i.toString) -> (i - 1)
      }

      // a reasonable approximation
      result += "Winter" -> 0
      result += "Spring" -> 3
      result += "Summer" -> 6
      result += "Fall" -> 9

    }
    val r = result.toMap
    r ++ r.map({
      case (a, b) => (a.toLowerCase, b)
    }) ++ r.map({
      case (a, b) => (a.toUpperCase, b)
    })
  }

  def parseMonthZeroBased(s: String) = zeroBasedMonthsByName(s.trim)

  def parseMonthOneBased(s: String) = zeroBasedMonthsByName(s.trim) + 1
}

