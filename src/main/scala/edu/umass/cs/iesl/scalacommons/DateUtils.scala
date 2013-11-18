package edu.umass.cs.iesl.scalacommons

import collection.mutable
import java.util.Locale
import java.text.DateFormatSymbols
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

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
      result += "Christmas" -> 12

    }
    val r = result.toMap
    r ++ r.map({
      case (a, b) => (a.toLowerCase, b)
    }) ++ r.map({
      case (a, b) => (a.toUpperCase, b)
    })
  }

  def parseMonthZeroBased(s: String): Option[Int] = zeroBasedMonthsByName.get(s.trim)

  def parseMonthOneBased(s: String): Option[Int] = zeroBasedMonthsByName.get(s.trim).map(_ + 1)

  def formatInterval(x: Option[DateTime], y: Option[DateTime]): String = {

    val yearOnlyFormat = DateTimeFormat.forPattern("yyyy")
    val monthAndYearFormat = DateTimeFormat.forPattern("MMM yyyy")
    val yearformat = DateTimeFormat.forPattern("dd MMM yyyy")
    val monthformat = DateTimeFormat.forPattern("dd MMM")
    val dayformat = DateTimeFormat.forPattern("dd")

    (x, y) match {
      case (None, None) => ""
      case (Some(x), None) => yearformat.print(x)
      case (None, Some(y)) => yearformat.print(y)
      case (Some(x), Some(y)) => {
        if (x == y) yearformat.print(x)
        else {
          if (x.year != y.year) {
            yearformat.print(x) + " - " + yearformat.print(y)
          }
          else if (x.monthOfYear() != y.monthOfYear()) {
            monthformat.print(x) + " - " + monthformat.print(y) + ", " + yearOnlyFormat.print(x)
          }
          else if (x.dayOfMonth() != y.dayOfMonth()) {
            dayformat.print(x) + " - " + dayformat.print(y) + ", " + monthAndYearFormat.print(x)
          }
          else yearformat.print(x)
        }
      }
    }
  }
}

