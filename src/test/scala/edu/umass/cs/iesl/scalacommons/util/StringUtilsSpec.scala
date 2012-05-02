package edu.umass.cs.iesl.scalacommons
package util

import org.specs2.mutable

import scalaz._, scalaz.{Scalaz => Z}, Z.{node => _, _}

import StringOps._
import FileOps._

import layout.boxter.Boxes._


object StringUtilsSpec extends mutable.Specification {

  "border stripping" should {
    "correctly remove these borders" in {
      val strs = """
      | xx |
      """
      val borderless = stripBorder(strs)
      borderless must_== " xx "
    }

    "bork on these borders" in {
      todo
    }
  }

}
