package edu.umass.cs.iesl.scalacommons


object StringUtils {
  implicit def emptyStringToNone(s: String): Option[String] = if (s.trim.isEmpty) None else Some(s)

  implicit def stringToOptionInt(s: String): Option[Int] = if (s.trim.isEmpty) None else Some(s.toInt)


}
