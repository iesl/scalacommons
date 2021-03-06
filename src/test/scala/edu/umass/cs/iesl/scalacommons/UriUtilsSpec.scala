package edu.umass.cs.iesl.scalacommons

import org.specs2.mutable

object UriUtilsSpec extends mutable.Specification {
  import UriUtils._

  val q0args = Seq("a" -> "1", "b" -> 2)
  val q0 = mkQuery(q0args:_*)

  val q1args = Seq("b" -> 2, "c" -> true, "d" -> false)
  val q1 = mkQuery(q1args:_*)

  "uri utils" should {
    "format params" in {
      mkQuery("a" -> "1", "b" -> "2") must_== "a=1&b=2"
      mkQuery("a" -> "1") must_== "a=1"
      q1 must_== "b=2&c=true&d=false"
    }

    "construct uris" in {
      uri("a/b/c", q0) must_== "a/b/c?a=1&b=2" 
    }

    "add query args to uri" in {
      todo
    }
  }

}
