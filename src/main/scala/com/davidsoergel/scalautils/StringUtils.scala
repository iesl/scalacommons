package com.davidsoergel.scalautils

object StringUtils {
  implicit def emptyStringToNone(s: String): Option[String] = {
    s match {
      case "" => None;
      case x => Some(x)
    }
  }
}
