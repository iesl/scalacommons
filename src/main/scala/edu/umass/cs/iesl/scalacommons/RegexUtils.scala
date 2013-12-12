package edu.umass.cs.iesl.scalacommons

import scala.util.matching.Regex

//http://stackoverflow.com/questions/3021813/how-to-check-whether-a-string-fully-matches-a-regex-in-scala
object RegexUtils {

  class RichRegex(underlying: Regex) {
    def matches(s: String) = underlying.pattern.matcher(s).matches
  }

  implicit def regexToRichRegex(r: Regex) = new RichRegex(r)
}

