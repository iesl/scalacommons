package edu.umass.cs.iesl.scalacommons


object StringUtils {
  implicit def emptyStringToNone(s: String): Option[String] = {
    s match {
      case "" => None;
      case x => Some(x)
    }
  }

  implicit def stringToOptionInt(s: String): Option[Int] = {
    s match {
      case "" => None;
      case x => Some(x.toInt)
    }
  }


}
