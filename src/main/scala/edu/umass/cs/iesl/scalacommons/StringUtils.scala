package edu.umass.cs.iesl.scalacommons

import java.util.regex.Pattern

object StringUtils {
  implicit def toOptionNonempty(s: String): Option[NonemptyString] = if (s.trim.isEmpty) None else Some(new NonemptyString(s.trim))

  // don't make this implicit; that would mask implicit conversions in Predef, providing String.size, String.nonEmpty, etc.
  //def toSingletonSetNonempty(s: String): Set[NonemptyString] = toOptionNonempty(s).toSet

  implicit def toSetNonempty[T <: Set[String]](ss: T): Set[NonemptyString] = ss.flatMap(toOptionNonempty)

  implicit def toSeqNonempty[T <: Seq[String]](ss: T): Seq[NonemptyString] = ss.flatMap(toOptionNonempty)


  //** need to understand CanBuildFrom etc. to make this work right
  //implicit def toTraversableNonempty[T <: Traversable[String]](ss: T): T[NonemptyString] = ss.flatMap(toOptionNonempty)

  //def toTraversableNonempty2[B, That, Repr](ss: B)(implicit bf: CanBuildFrom[Repr, B, That]): That = {}
  // def flatMap[B, That](f: A => GenTraversableOnce[B])(implicit bf: CanBuildFrom[Repr, B, That]): That = {}

  implicit def stringToOptionInt(s: String): Option[Int] = if (s.trim.isEmpty) None else Some(s.toInt)

  implicit def enrichString(s: String): RichString = new RichString(s)

  //** just use NonemptyString.unapply
  implicit def unwrapNonemptyString(n: NonemptyString): String = n.s

  // this is bad because they confound map, size, etc. operations from String and Option
  //implicit def unwrapNonemptyString(n: Option[NonemptyString]): String = n.map(unwrapNonemptyString).getOrElse("")

  //implicit def wrapNonemptyString(s: String) = NonemptyString(s)
}

object RichString {

  final private val deAccentPattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
}

class RichString(val s: String) {

  import java.text.Normalizer
  import RichString._

  def maskNewlines: String = s.replaceAll("[\\n\\r]+", " ")

  def maskNewlinesAndTabs: String = s.replaceAll("[\\n\\r\\t]+", " ")

  def stripWhitespace: String = s.replaceAll("\\s", "")

  def maskPunctuation: String = s.replaceAll("\\p{Punct}+", " ")

  def stripPunctuation: String = s.replaceAll("\\p{Punct}+", "")

  def maskAllButWord: String = s.replaceAll("[^\\w\\s]+", " ")

  def stripVowels: String = s.replaceAll("[AEIOUaeiou]", "")

  def collapseWhitespace: String = s.replaceAll("\\s+", " ")

  def opt: Option[NonemptyString] = StringUtils.toOptionNonempty(s)

  def n: NonemptyString = new NonemptyString(s.trim)

  def just: Set[NonemptyString] = opt.toSet

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
