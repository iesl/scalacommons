package edu.umass.cs.iesl.scalacommons


object StringUtils {
  implicit def emptyStringToNone(s: String): Option[String] = if (s.trim.isEmpty) None else Some(s)

  implicit def stringToOptionInt(s: String): Option[Int] = if (s.trim.isEmpty) None else Some(s.toInt)

  implicit def enrichString(s: String) = new RichString(s)
}

class RichString(val s: String) {
  def removeNewlines: String = s.replaceAll("[\\n\\r]", " ")
  def removePunctuation: String = s.replaceAll("\\p{Punct}", " ")
  def removeAllButWord: String = s.replaceAll("[^\\w\\s]", " ")
  def collapseWhitespace: String = s.replaceAll("\\s", " ")
}
