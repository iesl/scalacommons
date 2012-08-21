package edu.umass.cs.iesl.scalacommons

import java.util.regex.Pattern

object StringUtils {
  implicit def toOptionNonempty(s: String): Option[NonemptyString] = if (s.trim.isEmpty) None else Some(new NonemptyString(s.trim))

  implicit def toTraversableNonempty[T <: Traversable[String]](ss: T): Traversable[NonemptyString] = ss.flatMap(toOptionNonempty)

  implicit def stringToOptionInt(s: String): Option[Int] = if (s.trim.isEmpty) None else Some(s.toInt)

  implicit def enrichString(s: String): RichString = new RichString(s)

  //** just use NonemptyString.unapply
  implicit def unwrapNonemptyString(n: NonemptyString): String = n.s

  implicit def unwrapNonemptyString(n: Option[NonemptyString]): String = n.map(unwrapNonemptyString).getOrElse("")

  //implicit def wrapNonemptyString(s: String) = NonemptyString(s)
}

object RichString {

  final private val deAccentPattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
}

class RichString(val s: String) {

  import java.text.Normalizer
  import RichString._

  def removeNewlines: String = s.replaceAll("[\\n\\r]+", " ")

  def removeNewlinesAndTabs: String = s.replaceAll("[\\n\\r\\t]+", " ")

  def removeWhitespace: String = s.replaceAll("\\s", "")

  def removePunctuation: String = s.replaceAll("\\p{Punct}+", " ")

  def removeAllButWord: String = s.replaceAll("[^\\w\\s]+", " ")

  def removeVowels: String = s.replaceAll("[AEIOUaeiou]", "")

  def collapseWhitespace: String = s.replaceAll("\\s+", " ")

  def opt: Option[NonemptyString] = StringUtils.toOptionNonempty(s)

  def n: NonemptyString = new NonemptyString(s.trim)

  //http://stackoverflow.com/questions/1008802/converting-symbols-accent-letters-to-english-alphabet
  // see also icu4j Transliterator-- better, but a 7 MB jar, yikes.
  def deAccent: String = {
    val nfdNormalizedString = Normalizer.normalize(s, Normalizer.Form.NFD)
    deAccentPattern.matcher(nfdNormalizedString).replaceAll("")
  }

  def isAllUpperCase: Boolean = {
    val lc = """[a-z]""".r
    val r = lc.findFirstIn(s)
    r.isEmpty
  }

  def isAllLowerCase: Boolean = {
    val lc = """[A-Z]""".r
    val r = lc.findFirstIn(s)
    r.isEmpty
  }
}

case class NonemptyString(s: String) {
  require(s.nonEmpty)

  override def toString = s

  override def equals(other: Any): Boolean = other match {
    case that: NonemptyString => this.s == that.s
    case _ => false
  }

  override def hashCode: Int = s.hashCode


  //def +(that:NonemptyString) = new NonemptyString(s + that.s)
}
