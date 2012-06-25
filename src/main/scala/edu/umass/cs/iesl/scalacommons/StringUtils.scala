package edu.umass.cs.iesl.scalacommons

object StringUtils
	{
	implicit def emptyStringToNone(s: String): Option[NonemptyString] = if (s.trim.isEmpty) None else Some(new NonemptyString(s.trim))

	implicit def stringToOptionInt(s: String): Option[Int] = if (s.trim.isEmpty) None else Some(s.toInt)

	implicit def enrichString(s: String) : RichString = new RichString(s)

	implicit def unwrapNonemptyString(n: NonemptyString) : String = n.s

	implicit def unwrapNonemptyString(n: Option[NonemptyString]): String = n.map(unwrapNonemptyString).getOrElse("")

	//implicit def wrapNonemptyString(s: String) = NonemptyString(s)
	}

class RichString(val s: String)
	{
	def removeNewlines: String = s.replaceAll("[\\n\\r]+", " ")

	def removeNewlinesAndTabs: String = s.replaceAll("[\\n\\r\\t]+", " ")

	def removePunctuation: String = s.replaceAll("\\p{Punct}+", " ")

	def removeAllButWord: String = s.replaceAll("[^\\w\\s]+", " ")

	def collapseWhitespace: String = s.replaceAll("\\s+", " ")
	}

case class NonemptyString(s: String)
	{
	require(s.nonEmpty)

	override def toString = s

	// make equality depend only on the rectangle, so that there is only one representative at a time in the priority queue.
	override def equals(other: Any): Boolean = other match
	{
		case that: NonemptyString => this.s == that.s
		case _ => false
	}

	override def hashCode: Int = s.hashCode


	//def +(that:NonemptyString) = new NonemptyString(s + that.s)
	}
